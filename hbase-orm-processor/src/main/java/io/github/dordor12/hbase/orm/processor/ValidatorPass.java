package io.github.dordor12.hbase.orm.processor;

import io.github.dordor12.hbase.orm.annotation.*;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * Compile-time validation of @Table-annotated entities.
 * All old runtime exceptions become compile errors.
 */
public class ValidatorPass {

    private final Messager messager;
    private final Elements elements;
    private final Types types;

    public ValidatorPass(Messager messager, Elements elements, Types types) {
        this.messager = messager;
        this.elements = elements;
        this.types = types;
    }

    /**
     * Validate the entity and return true if valid.
     */
    public boolean validate(TypeElement typeElement) {
        boolean valid = true;

        // Must have public no-arg constructor
        if (!hasPublicNoArgConstructor(typeElement)) {
            error(typeElement, "@Table class '%s' must have a public no-arg constructor",
                    typeElement.getSimpleName());
            valid = false;
        }

        // Collect @Table annotation
        Table tableAnn = typeElement.getAnnotation(Table.class);
        if (tableAnn == null) {
            error(typeElement, "Class '%s' is missing @Table annotation", typeElement.getSimpleName());
            return false;
        }

        // Validate table name
        if (tableAnn.name().isBlank()) {
            error(typeElement, "@Table name cannot be empty on '%s'", typeElement.getSimpleName());
            valid = false;
        }

        // Collect family names
        Set<String> familyNames = new HashSet<>();
        for (ColumnFamily cf : tableAnn.families()) {
            if (cf.name().isBlank()) {
                error(typeElement, "@ColumnFamily name cannot be empty on '%s'",
                        typeElement.getSimpleName());
                valid = false;
            }
            if (cf.versions() < 1) {
                error(typeElement, "@ColumnFamily '%s' versions must be >= 1", cf.name());
                valid = false;
            }
            if (!familyNames.add(cf.name())) {
                error(typeElement, "Duplicate column family name '%s' on '%s'",
                        cf.name(), typeElement.getSimpleName());
                valid = false;
            }
        }

        // Collect all fields (including from @MappedSuperclass hierarchy)
        List<VariableElement> allFields = collectFields(typeElement);

        // Check for @RowKey / @RowKeyComponent
        boolean hasRowKey = false;
        boolean hasRowKeyComponent = false;
        for (VariableElement field : allFields) {
            if (field.getAnnotation(RowKey.class) != null) hasRowKey = true;
            if (field.getAnnotation(RowKeyComponent.class) != null) hasRowKeyComponent = true;
        }

        if (hasRowKey && hasRowKeyComponent) {
            error(typeElement, "Cannot use both @RowKey and @RowKeyComponent on '%s'",
                    typeElement.getSimpleName());
            valid = false;
        }
        if (!hasRowKey && !hasRowKeyComponent) {
            error(typeElement, "Must have @RowKey or @RowKeyComponent on '%s'",
                    typeElement.getSimpleName());
            valid = false;
        }

        // Validate column fields
        Set<String> columnKeys = new HashSet<>();
        for (VariableElement field : allFields) {
            Column col = field.getAnnotation(Column.class);
            MultiVersion mv = field.getAnnotation(MultiVersion.class);

            if (col != null && mv != null) {
                error(field, "Field '%s' cannot have both @Column and @MultiVersion",
                        field.getSimpleName());
                valid = false;
                continue;
            }

            if (col != null) {
                valid &= validateColumnField(field, col, familyNames, columnKeys);
            }
            if (mv != null) {
                valid &= validateMultiVersionField(field, mv, familyNames, columnKeys);
            }
        }

        // Validate @RowKeyComponent ordering if present
        if (hasRowKeyComponent) {
            valid &= validateRowKeyComponents(typeElement, allFields);
        }

        return valid;
    }

    private boolean validateColumnField(VariableElement field, Column col,
                                         Set<String> familyNames, Set<String> columnKeys) {
        boolean valid = true;

        // Check primitive type
        if (field.asType().getKind().isPrimitive()) {
            error(field, "Field '%s' must not be a primitive type. Use wrapper types instead.",
                    field.getSimpleName());
            valid = false;
        }

        // Check family exists
        if (!familyNames.contains(col.family())) {
            error(field, "Column family '%s' on field '%s' is not declared in @Table families",
                    col.family(), field.getSimpleName());
            valid = false;
        }

        // Check duplicate family:qualifier
        String key = col.family() + ":" + col.qualifier();
        if (!columnKeys.add(key)) {
            error(field, "Duplicate column mapping '%s' on field '%s'",
                    key, field.getSimpleName());
            valid = false;
        }

        // Check accessor (getter/setter) for non-public fields
        if (!field.getModifiers().contains(Modifier.PUBLIC)) {
            TypeElement enclosing = (TypeElement) field.getEnclosingElement();
            valid &= checkAccessors(field, enclosing);
        }

        return valid;
    }

    private boolean validateMultiVersionField(VariableElement field, MultiVersion mv,
                                               Set<String> familyNames, Set<String> columnKeys) {
        boolean valid = true;

        // Check family exists
        if (!familyNames.contains(mv.family())) {
            error(field, "Column family '%s' on field '%s' is not declared in @Table families",
                    mv.family(), field.getSimpleName());
            valid = false;
        }

        // Check duplicate family:qualifier
        String key = mv.family() + ":" + mv.qualifier();
        if (!columnKeys.add(key)) {
            error(field, "Duplicate column mapping '%s' on field '%s'",
                    key, field.getSimpleName());
            valid = false;
        }

        // Must be NavigableMap<Long, T>
        if (!isNavigableMapLongValue(field.asType())) {
            error(field, "@MultiVersion field '%s' must be of type NavigableMap<Long, T>",
                    field.getSimpleName());
            valid = false;
        }

        // Check accessor for non-public fields
        if (!field.getModifiers().contains(Modifier.PUBLIC)) {
            TypeElement enclosing = (TypeElement) field.getEnclosingElement();
            valid &= checkAccessors(field, enclosing);
        }

        return valid;
    }

    private boolean validateRowKeyComponents(TypeElement typeElement, List<VariableElement> allFields) {
        boolean valid = true;
        Map<Integer, VariableElement> orderMap = new TreeMap<>();

        for (VariableElement field : allFields) {
            RowKeyComponent rkc = field.getAnnotation(RowKeyComponent.class);
            if (rkc != null) {
                if (orderMap.containsKey(rkc.order())) {
                    error(field, "Duplicate @RowKeyComponent order %d on '%s'",
                            rkc.order(), field.getSimpleName());
                    valid = false;
                } else {
                    orderMap.put(rkc.order(), field);
                }
            }
        }

        // Check contiguous ordering starting from 0
        int expected = 0;
        for (int order : orderMap.keySet()) {
            if (order != expected) {
                error(typeElement, "@RowKeyComponent orders must be contiguous starting from 0, " +
                        "but found gap at %d", expected);
                valid = false;
                break;
            }
            expected++;
        }

        return valid;
    }

    private boolean isNavigableMapLongValue(TypeMirror type) {
        if (type.getKind() != TypeKind.DECLARED) return false;
        DeclaredType dt = (DeclaredType) type;
        Element element = dt.asElement();
        String name = ((TypeElement) element).getQualifiedName().toString();

        if (!name.equals("java.util.NavigableMap") && !name.equals("java.util.TreeMap")) {
            // Check if it's a subtype of NavigableMap
            TypeMirror navMapType = elements.getTypeElement("java.util.NavigableMap").asType();
            if (!types.isAssignable(types.erasure(type), types.erasure(navMapType))) {
                return false;
            }
        }

        List<? extends TypeMirror> typeArgs = dt.getTypeArguments();
        if (typeArgs.size() != 2) return false;

        TypeMirror keyType = typeArgs.get(0);
        TypeElement longElement = elements.getTypeElement("java.lang.Long");
        return types.isSameType(keyType, longElement.asType());
    }

    private boolean checkAccessors(VariableElement field, TypeElement enclosing) {
        String fieldName = field.getSimpleName().toString();
        String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String getterName = "get" + capitalized;
        String setterName = "set" + capitalized;

        // Check for boolean getter variant
        String boolGetterName = "is" + capitalized;

        boolean hasGetter = false;
        boolean hasSetter = false;

        for (Element member : elements.getAllMembers(enclosing)) {
            if (member.getKind() != ElementKind.METHOD) continue;
            ExecutableElement method = (ExecutableElement) member;
            String methodName = method.getSimpleName().toString();

            if ((methodName.equals(getterName) || methodName.equals(boolGetterName))
                    && method.getParameters().isEmpty()) {
                hasGetter = true;
            }
            if (methodName.equals(setterName) && method.getParameters().size() == 1) {
                hasSetter = true;
            }
        }

        boolean valid = true;
        if (!hasGetter) {
            error(field, "Non-public field '%s' needs a getter method '%s()' or '%s()'",
                    fieldName, getterName, boolGetterName);
            valid = false;
        }
        if (!hasSetter) {
            error(field, "Non-public field '%s' needs a setter method '%s(...)'",
                    fieldName, setterName);
            valid = false;
        }
        return valid;
    }

    private boolean hasPublicNoArgConstructor(TypeElement typeElement) {
        for (Element enclosed : typeElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement ctor = (ExecutableElement) enclosed;
                if (ctor.getParameters().isEmpty()
                        && ctor.getModifiers().contains(Modifier.PUBLIC)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Collect all fields from the class and its @MappedSuperclass ancestors.
     */
    List<VariableElement> collectFields(TypeElement typeElement) {
        List<VariableElement> fields = new ArrayList<>();
        collectFieldsRecursive(typeElement, fields);
        return fields;
    }

    private void collectFieldsRecursive(TypeElement typeElement, List<VariableElement> fields) {
        // Walk up the hierarchy for @MappedSuperclass
        TypeMirror superclass = typeElement.getSuperclass();
        if (superclass.getKind() == TypeKind.DECLARED) {
            TypeElement superElement = (TypeElement) ((DeclaredType) superclass).asElement();
            if (superElement.getAnnotation(MappedSuperclass.class) != null) {
                collectFieldsRecursive(superElement, fields);
            }
        }

        // Add fields from this class
        for (Element enclosed : typeElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) enclosed;
                Set<Modifier> mods = field.getModifiers();
                if (mods.contains(Modifier.STATIC) || mods.contains(Modifier.TRANSIENT)) {
                    continue;
                }
                fields.add(field);
            }
        }
    }

    private void error(Element element, String format, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(format, args), element);
    }
}
