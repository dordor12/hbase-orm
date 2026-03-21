package io.github.dordor12.hbase.orm.processor;

import io.github.dordor12.hbase.orm.annotation.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

/**
 * Annotation processor that generates HBMapper implementations for @Table-annotated classes.
 */
@SupportedAnnotationTypes("io.github.dordor12.hbase.orm.annotation.Table")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class HBaseOrmProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elements;
    private Types types;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.elements = processingEnv.getElementUtils();
        this.types = processingEnv.getTypeUtils();
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@Table can only be applied to classes", element);
                continue;
            }

            TypeElement typeElement = (TypeElement) element;

            // Validate
            ValidatorPass validator = new ValidatorPass(messager, elements, types);
            if (!validator.validate(typeElement)) {
                continue; // Errors already reported
            }

            // Build IR
            EntityModel model = buildEntityModel(typeElement, validator);
            if (model == null) continue;

            // Generate mapper
            MapperGenerator generator = new MapperGenerator();
            try {
                generator.generate(model).writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Failed to generate mapper: " + e.getMessage(), typeElement);
            }
        }
        return true;
    }

    private EntityModel buildEntityModel(TypeElement typeElement, ValidatorPass validator) {
        Table tableAnn = typeElement.getAnnotation(Table.class);

        // Package and class names
        String qualifiedName = typeElement.getQualifiedName().toString();
        String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
        String className = typeElement.getSimpleName().toString();

        // Families
        Map<String, Integer> familiesAndVersions = new LinkedHashMap<>();
        for (ColumnFamily cf : tableAnn.families()) {
            familiesAndVersions.put(cf.name(), cf.versions());
        }

        // Row key codec flags
        Map<String, String> rowKeyCodecFlags = new LinkedHashMap<>();
        for (CodecFlag flag : tableAnn.rowKeyCodecFlags()) {
            rowKeyCodecFlags.put(flag.name(), flag.value());
        }

        // Collect all fields
        List<VariableElement> allFields = validator.collectFields(typeElement);

        // Build row key model
        RowKeyModel rowKeyModel = buildRowKeyModel(allFields, typeElement);
        if (rowKeyModel == null) return null;

        // Build field models for @Column / @MultiVersion
        List<FieldModel> fieldModels = new ArrayList<>();
        for (VariableElement field : allFields) {
            Column col = field.getAnnotation(Column.class);
            MultiVersion mv = field.getAnnotation(MultiVersion.class);

            if (col != null) {
                fieldModels.add(buildColumnFieldModel(field, col));
            } else if (mv != null) {
                fieldModels.add(buildMultiVersionFieldModel(field, mv));
            }
        }

        return new EntityModel(
                packageName, className, qualifiedName,
                tableAnn.name(), tableAnn.namespace(),
                familiesAndVersions, rowKeyCodecFlags,
                rowKeyModel, fieldModels
        );
    }

    private RowKeyModel buildRowKeyModel(List<VariableElement> allFields, TypeElement typeElement) {
        VariableElement simpleRowKey = null;
        List<RowKeyModel.Component> components = new ArrayList<>();

        for (VariableElement field : allFields) {
            RowKey rk = field.getAnnotation(RowKey.class);
            RowKeyComponent rkc = field.getAnnotation(RowKeyComponent.class);

            if (rk != null) {
                simpleRowKey = field;
            }
            if (rkc != null) {
                String fieldName = field.getSimpleName().toString();
                components.add(new RowKeyModel.Component(
                        rkc.order(),
                        fieldName,
                        field.asType(),
                        rkc.delimiter(),
                        getterName(field),
                        setterName(field)
                ));
            }
        }

        if (simpleRowKey != null) {
            String fieldName = simpleRowKey.getSimpleName().toString();
            return new RowKeyModel.Simple(
                    fieldName,
                    simpleRowKey.asType(),
                    getterName(simpleRowKey),
                    setterName(simpleRowKey)
            );
        }

        if (!components.isEmpty()) {
            components.sort(Comparator.comparingInt(RowKeyModel.Component::order));
            return new RowKeyModel.Composite(components);
        }

        return null;
    }

    private FieldModel buildColumnFieldModel(VariableElement field, Column col) {
        String fieldName = field.getSimpleName().toString();
        Map<String, String> flags = new LinkedHashMap<>();
        for (CodecFlag f : col.codecFlags()) {
            flags.put(f.name(), f.value());
        }

        return new FieldModel(
                fieldName,
                col.family(),
                col.qualifier(),
                field.asType(),
                field.asType(), // valueType same as fieldType for single-version
                false,
                flags,
                getterName(field),
                setterName(field),
                !field.getModifiers().contains(Modifier.PUBLIC)
        );
    }

    private FieldModel buildMultiVersionFieldModel(VariableElement field, MultiVersion mv) {
        String fieldName = field.getSimpleName().toString();
        Map<String, String> flags = new LinkedHashMap<>();
        for (CodecFlag f : mv.codecFlags()) {
            flags.put(f.name(), f.value());
        }

        // Extract the value type T from NavigableMap<Long, T>
        TypeMirror valueType = extractMultiVersionValueType(field.asType());

        return new FieldModel(
                fieldName,
                mv.family(),
                mv.qualifier(),
                field.asType(),
                valueType,
                true,
                flags,
                getterName(field),
                setterName(field),
                !field.getModifiers().contains(Modifier.PUBLIC)
        );
    }

    private TypeMirror extractMultiVersionValueType(TypeMirror type) {
        if (type.getKind() == TypeKind.DECLARED) {
            DeclaredType dt = (DeclaredType) type;
            List<? extends TypeMirror> typeArgs = dt.getTypeArguments();
            if (typeArgs.size() == 2) {
                return typeArgs.get(1); // The T in NavigableMap<Long, T>
            }
        }
        // Fallback
        return elements.getTypeElement("java.lang.Object").asType();
    }

    private String getterName(VariableElement field) {
        String name = field.getSimpleName().toString();
        String capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);

        // Check if it's a boolean for "is" prefix
        TypeMirror fieldType = field.asType();
        if (fieldType.toString().equals("java.lang.Boolean")) {
            // Check if isXxx method exists on the enclosing type
            TypeElement enclosing = (TypeElement) field.getEnclosingElement();
            for (Element member : elements.getAllMembers(enclosing)) {
                if (member.getKind() == ElementKind.METHOD
                        && member.getSimpleName().toString().equals("is" + capitalized)
                        && ((ExecutableElement) member).getParameters().isEmpty()) {
                    return "is" + capitalized;
                }
            }
        }

        return "get" + capitalized;
    }

    private String setterName(VariableElement field) {
        String name = field.getSimpleName().toString();
        return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
