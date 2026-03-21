package io.github.dordor12.hbase.orm.processor;

import com.squareup.javapoet.*;
import io.github.dordor12.hbase.orm.codec.Codec;
import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

/**
 * Generates the *HBMapper implementation class using JavaPoet.
 */
public class MapperGenerator {

    public JavaFile generate(EntityModel model) {
        TypeName rowKeyType = resolveRowKeyType(model);
        TypeName entityType = ClassName.bestGuess(model.qualifiedName());

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(model.mapperClassName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(HBaseMapper.class), rowKeyType, entityType))
                .addAnnotation(AnnotationSpec.builder(
                        ClassName.get("javax.annotation.processing", "Generated"))
                        .addMember("value", "$S", "io.github.dordor12.hbase.orm.processor.HBaseOrmProcessor")
                        .build());

        // Add codec field
        classBuilder.addField(FieldSpec.builder(ClassName.get(Codec.class), "codec",
                Modifier.PRIVATE, Modifier.FINAL).build());

        // Add static constant fields for family/qualifier bytes
        addStaticConstants(classBuilder, model);

        // Constructor
        classBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(Codec.class), "codec")
                .addStatement("this.codec = codec")
                .build());

        // Generate all mapper methods
        classBuilder.addMethod(generateWriteAsPut(model, entityType));
        classBuilder.addMethod(generateReadFromResult(model, entityType, rowKeyType));
        classBuilder.addMethod(generateWriteAsResult(model, entityType));
        classBuilder.addMethod(generateReadFromPut(model, entityType, rowKeyType));
        classBuilder.addMethod(generateComposeRowKey(model, entityType));
        classBuilder.addMethod(generateParseRowKey(model, entityType));
        classBuilder.addMethod(generateGetRowKey(model, entityType, rowKeyType));
        classBuilder.addMethod(generateGetTableName(model));
        classBuilder.addMethod(generateGetColumnFamiliesAndVersions(model));
        classBuilder.addMethod(generateGetColumn(model));

        // Add helper methods for codec flags
        addCodecFlagHelpers(classBuilder, model);

        return JavaFile.builder(model.packageName(), classBuilder.build())
                .indent("    ")
                .build();
    }

    // ─── Static Constants ────────────────────────────────────────────

    private void addStaticConstants(TypeSpec.Builder classBuilder, EntityModel model) {
        // Table name constant
        classBuilder.addField(FieldSpec.builder(String.class, "TABLE_NAME",
                        Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", model.fullTableName())
                .build());

        // Family byte constants
        Set<String> families = new LinkedHashSet<>();
        for (FieldModel field : model.fields()) {
            families.add(field.family());
        }
        for (String family : families) {
            classBuilder.addField(FieldSpec.builder(byte[].class,
                            "FAMILY_" + family.toUpperCase(),
                            Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$T.toBytes($S)", Bytes.class, family)
                    .build());
        }

        // Qualifier byte constants for each field
        for (FieldModel field : model.fields()) {
            classBuilder.addField(FieldSpec.builder(byte[].class,
                            "QUAL_" + field.fieldName().toUpperCase(),
                            Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$T.toBytes($S)", Bytes.class, field.qualifier())
                    .build());
        }
    }

    // ─── writeAsPut ──────────────────────────────────────────────────

    private MethodSpec generateWriteAsPut(EntityModel model, TypeName entityType) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("writeAsPut")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(Put.class))
                .addParameter(entityType, "entity");

        method.addStatement("byte[] rowKey = composeRowKey(entity)");
        method.addStatement("$T put = new $T(rowKey)", Put.class, Put.class);

        for (FieldModel field : model.fields()) {
            String familyConst = "FAMILY_" + field.family().toUpperCase();
            String qualConst = "QUAL_" + field.fieldName().toUpperCase();
            String getter = "entity." + field.getterName() + "()";

            if (field.multiVersioned()) {
                // Multi-version: iterate NavigableMap<Long, T>
                method.beginControlFlow("if ($L != null)", getter);
                method.beginControlFlow("for ($T.Entry<$T, ?> entry : $L.entrySet())",
                        Map.class, Long.class, getter);
                method.addStatement("byte[] val = codec.serialize(entry.getValue(), $L)",
                        codecFlagsExpr(field));
                method.beginControlFlow("if (val != null)");
                method.addStatement("put.addColumn($L, $L, entry.getKey(), val)",
                        familyConst, qualConst);
                method.endControlFlow();
                method.endControlFlow();
                method.endControlFlow();
            } else {
                // Single version
                method.beginControlFlow("if ($L != null)", getter);
                method.addStatement("byte[] val = codec.serialize($L, $L)",
                        getter, codecFlagsExpr(field));
                method.beginControlFlow("if (val != null)");
                method.addStatement("put.addColumn($L, $L, val)", familyConst, qualConst);
                method.endControlFlow();
                method.endControlFlow();
            }
        }

        method.addStatement("return put");
        return method.build();
    }

    // ─── readFromResult ──────────────────────────────────────────────

    private MethodSpec generateReadFromResult(EntityModel model, TypeName entityType, TypeName rowKeyType) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("readFromResult")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityType)
                .addParameter(ClassName.get(Result.class), "result");

        method.addStatement("$T entity = new $T()", entityType, entityType);

        // Parse row key
        method.addStatement("parseRowKey(result.getRow(), entity)");

        // Read each field
        for (FieldModel field : model.fields()) {
            String familyConst = "FAMILY_" + field.family().toUpperCase();
            String qualConst = "QUAL_" + field.fieldName().toUpperCase();
            String setter = "entity." + field.setterName();

            if (field.multiVersioned()) {
                // Multi-version: get all cells for this column
                method.addStatement("$T<$T> cells_$L = result.getColumnCells($L, $L)",
                        List.class, Cell.class, field.fieldName(), familyConst, qualConst);
                method.beginControlFlow("if (cells_$L != null && !cells_$L.isEmpty())",
                        field.fieldName(), field.fieldName());
                method.addStatement("$T<$T, $T> map_$L = new $T<>()",
                        NavigableMap.class, Long.class,
                        TypeName.get(getMultiVersionValueType(field)),
                        field.fieldName(), TreeMap.class);
                method.beginControlFlow("for ($T cell : cells_$L)", Cell.class, field.fieldName());
                method.addStatement("byte[] val = $T.cloneValue(cell)", CellUtil.class);
                method.addStatement("$T deserialized = ($T) codec.deserialize(val, $L, $L)",
                        TypeName.get(getMultiVersionValueType(field)),
                        TypeName.get(getMultiVersionValueType(field)),
                        typeClassExpr(getMultiVersionValueType(field)),
                        codecFlagsExpr(field));
                method.addStatement("map_$L.put(cell.getTimestamp(), deserialized)", field.fieldName());
                method.endControlFlow();
                method.addStatement("$L(map_$L)", setter, field.fieldName());
                method.endControlFlow();
            } else {
                // Single version
                method.addStatement("byte[] bytes_$L = result.getValue($L, $L)",
                        field.fieldName(), familyConst, qualConst);
                method.beginControlFlow("if (bytes_$L != null)", field.fieldName());
                method.addStatement("$L(($T) codec.deserialize(bytes_$L, $L, $L))",
                        setter,
                        TypeName.get(field.fieldType()),
                        field.fieldName(),
                        typeClassExpr(field.fieldType()),
                        codecFlagsExpr(field));
                method.endControlFlow();
            }
        }

        method.addStatement("return entity");
        return method.build();
    }

    // ─── writeAsResult ───────────────────────────────────────────────

    private MethodSpec generateWriteAsResult(EntityModel model, TypeName entityType) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("writeAsResult")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(Result.class))
                .addParameter(entityType, "entity");

        method.addStatement("$T put = writeAsPut(entity)", Put.class);
        method.addStatement("$T<$T> cells = new $T<>()", List.class, Cell.class, ArrayList.class);
        method.beginControlFlow("for ($T<$T> familyCells : put.getFamilyCellMap().values())",
                List.class, Cell.class);
        method.addStatement("cells.addAll(familyCells)");
        method.endControlFlow();
        method.addStatement("return $T.create(cells)", Result.class);

        return method.build();
    }

    // ─── readFromPut ─────────────────────────────────────────────────

    private MethodSpec generateReadFromPut(EntityModel model, TypeName entityType, TypeName rowKeyType) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("readFromPut")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityType)
                .addParameter(ClassName.get(Put.class), "put");

        method.addStatement("$T entity = new $T()", entityType, entityType);
        method.addStatement("parseRowKey(put.getRow(), entity)");

        for (FieldModel field : model.fields()) {
            String familyConst = "FAMILY_" + field.family().toUpperCase();
            String qualConst = "QUAL_" + field.fieldName().toUpperCase();
            String setter = "entity." + field.setterName();

            method.addStatement("$T<$T> cells_$L = put.get($L, $L)",
                    List.class, Cell.class, field.fieldName(), familyConst, qualConst);

            if (field.multiVersioned()) {
                method.beginControlFlow("if (cells_$L != null && !cells_$L.isEmpty())",
                        field.fieldName(), field.fieldName());
                method.addStatement("$T<$T, $T> map_$L = new $T<>()",
                        NavigableMap.class, Long.class,
                        TypeName.get(getMultiVersionValueType(field)),
                        field.fieldName(), TreeMap.class);
                method.beginControlFlow("for ($T cell : cells_$L)", Cell.class, field.fieldName());
                method.addStatement("byte[] val = $T.cloneValue(cell)", CellUtil.class);
                method.addStatement("$T deserialized = ($T) codec.deserialize(val, $L, $L)",
                        TypeName.get(getMultiVersionValueType(field)),
                        TypeName.get(getMultiVersionValueType(field)),
                        typeClassExpr(getMultiVersionValueType(field)),
                        codecFlagsExpr(field));
                method.addStatement("map_$L.put(cell.getTimestamp(), deserialized)", field.fieldName());
                method.endControlFlow();
                method.addStatement("$L(map_$L)", setter, field.fieldName());
                method.endControlFlow();
            } else {
                method.beginControlFlow("if (cells_$L != null && !cells_$L.isEmpty())",
                        field.fieldName(), field.fieldName());
                method.addStatement("byte[] val = $T.cloneValue(cells_$L.get(0))",
                        CellUtil.class, field.fieldName());
                method.addStatement("$L(($T) codec.deserialize(val, $L, $L))",
                        setter,
                        TypeName.get(field.fieldType()),
                        typeClassExpr(field.fieldType()),
                        codecFlagsExpr(field));
                method.endControlFlow();
            }
        }

        method.addStatement("return entity");
        return method.build();
    }

    // ─── composeRowKey ───────────────────────────────────────────────

    private MethodSpec generateComposeRowKey(EntityModel model, TypeName entityType) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("composeRowKey")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(byte[].class)
                .addParameter(entityType, "entity");

        RowKeyModel rkm = model.rowKeyModel();

        if (rkm instanceof RowKeyModel.Simple simple) {
            // Simple row key: serialize the field directly
            method.addStatement("return codec.serialize(entity.$L(), $L)",
                    simple.getterName(), codecFlagsExprForRowKey(model));
        } else if (rkm instanceof RowKeyModel.Composite composite) {
            // Composite: join components with delimiters
            method.addStatement("$T sb = new $T()", StringBuilder.class, StringBuilder.class);
            List<RowKeyModel.Component> components = composite.components();
            for (int i = 0; i < components.size(); i++) {
                RowKeyModel.Component comp = components.get(i);
                if (i > 0) {
                    // Use the delimiter from the PREVIOUS component
                    method.addStatement("sb.append($S)", components.get(i - 1).delimiter());
                }
                method.addStatement("sb.append(String.valueOf(entity.$L()))", comp.getterName());
            }
            method.addStatement("return $T.toBytes(sb.toString())", Bytes.class);
        }

        return method.build();
    }

    // ─── parseRowKey ─────────────────────────────────────────────────

    private MethodSpec generateParseRowKey(EntityModel model, TypeName entityType) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("parseRowKey")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(byte[].class, "rowKeyBytes")
                .addParameter(entityType, "entity");

        RowKeyModel rkm = model.rowKeyModel();

        if (rkm instanceof RowKeyModel.Simple simple) {
            TypeMirror type = simple.fieldType();
            method.addStatement("entity.$L(($T) codec.deserialize(rowKeyBytes, $L, $L))",
                    simple.setterName(),
                    TypeName.get(type),
                    typeClassExpr(type),
                    codecFlagsExprForRowKey(model));
        } else if (rkm instanceof RowKeyModel.Composite composite) {
            List<RowKeyModel.Component> components = composite.components();
            // Find the delimiter (use first component's delimiter)
            String delimiter = components.get(0).delimiter();

            method.addStatement("String rowKeyStr = $T.toString(rowKeyBytes)", Bytes.class);

            // Split by delimiter with limit to handle empty trailing parts
            method.addStatement("String[] parts = rowKeyStr.split($S, $L)",
                    escapeRegex(delimiter), components.size());

            for (int i = 0; i < components.size(); i++) {
                RowKeyModel.Component comp = components.get(i);
                String parseExpr = generateParseExpression(comp.fieldType(), "parts[" + i + "]");
                method.addStatement("entity.$L($L)", comp.setterName(), parseExpr);
            }
        }

        return method.build();
    }

    // ─── getRowKey ───────────────────────────────────────────────────

    private MethodSpec generateGetRowKey(EntityModel model, TypeName entityType, TypeName rowKeyType) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("getRowKey")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(rowKeyType)
                .addParameter(entityType, "entity");

        RowKeyModel rkm = model.rowKeyModel();

        if (rkm instanceof RowKeyModel.Simple simple) {
            method.addStatement("return entity.$L()", simple.getterName());
        } else if (rkm instanceof RowKeyModel.Composite composite) {
            // Composite keys always return String
            List<RowKeyModel.Component> components = composite.components();
            method.addStatement("$T sb = new $T()", StringBuilder.class, StringBuilder.class);
            for (int i = 0; i < components.size(); i++) {
                RowKeyModel.Component comp = components.get(i);
                if (i > 0) {
                    method.addStatement("sb.append($S)", components.get(i - 1).delimiter());
                }
                method.addStatement("sb.append(String.valueOf(entity.$L()))", comp.getterName());
            }
            method.addStatement("return sb.toString()");
        }

        return method.build();
    }

    // ─── getTableName ────────────────────────────────────────────────

    private MethodSpec generateGetTableName(EntityModel model) {
        return MethodSpec.methodBuilder("getTableName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return TABLE_NAME")
                .build();
    }

    // ─── getColumnFamiliesAndVersions ────────────────────────────────

    private MethodSpec generateGetColumnFamiliesAndVersions(EntityModel model) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("getColumnFamiliesAndVersions")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(Map.class, String.class, Integer.class));

        method.addStatement("$T<$T, $T> map = new $T<>()",
                Map.class, String.class, Integer.class, LinkedHashMap.class);
        for (Map.Entry<String, Integer> entry : model.familiesAndVersions().entrySet()) {
            method.addStatement("map.put($S, $L)", entry.getKey(), entry.getValue());
        }
        method.addStatement("return map");

        return method.build();
    }

    // ─── getColumn ───────────────────────────────────────────────────

    private MethodSpec generateGetColumn(EntityModel model) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("getColumn")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(byte[][].class)
                .addParameter(String.class, "fieldName");

        boolean first = true;
        for (FieldModel field : model.fields()) {
            String familyConst = "FAMILY_" + field.family().toUpperCase();
            String qualConst = "QUAL_" + field.fieldName().toUpperCase();

            if (first) {
                method.beginControlFlow("if ($S.equals(fieldName))", field.fieldName());
                first = false;
            } else {
                method.nextControlFlow("else if ($S.equals(fieldName))", field.fieldName());
            }
            method.addStatement("return new byte[][] { $L, $L }", familyConst, qualConst);
        }
        if (!first) {
            method.endControlFlow();
        }
        method.addStatement("throw new $T($S + fieldName)",
                IllegalArgumentException.class, "Unknown field: ");
        return method.build();
    }

    // ─── Codec Flag Helpers ──────────────────────────────────────────

    private void addCodecFlagHelpers(TypeSpec.Builder classBuilder, EntityModel model) {
        // Generate static codec flag maps for fields that have flags
        Set<String> generated = new HashSet<>();
        for (FieldModel field : model.fields()) {
            if (!field.codecFlags().isEmpty()) {
                String constName = "FLAGS_" + field.fieldName().toUpperCase();
                if (generated.add(constName)) {
                    CodeBlock.Builder init = CodeBlock.builder()
                            .add("$T.of(", Map.class);
                    int i = 0;
                    for (Map.Entry<String, String> entry : field.codecFlags().entrySet()) {
                        if (i > 0) init.add(", ");
                        init.add("$S, $S", entry.getKey(), entry.getValue());
                        i++;
                    }
                    init.add(")");

                    classBuilder.addField(FieldSpec.builder(
                                    ParameterizedTypeName.get(Map.class, String.class, String.class),
                                    constName,
                                    Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                            .initializer(init.build())
                            .build());
                }
            }
        }

        // Row key codec flags
        if (!model.rowKeyCodecFlags().isEmpty()) {
            CodeBlock.Builder init = CodeBlock.builder()
                    .add("$T.of(", Map.class);
            int i = 0;
            for (Map.Entry<String, String> entry : model.rowKeyCodecFlags().entrySet()) {
                if (i > 0) init.add(", ");
                init.add("$S, $S", entry.getKey(), entry.getValue());
                i++;
            }
            init.add(")");

            classBuilder.addField(FieldSpec.builder(
                            ParameterizedTypeName.get(Map.class, String.class, String.class),
                            "ROW_KEY_FLAGS",
                            Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer(init.build())
                    .build());
        }
    }

    // ─── Helper Methods ──────────────────────────────────────────────

    private TypeName resolveRowKeyType(EntityModel model) {
        RowKeyModel rkm = model.rowKeyModel();
        if (rkm instanceof RowKeyModel.Simple simple) {
            return TypeName.get(simple.fieldType());
        }
        // Composite keys always use String
        return ClassName.get(String.class);
    }

    private CodeBlock codecFlagsExpr(FieldModel field) {
        if (field.codecFlags().isEmpty()) {
            return CodeBlock.of("$T.of()", Map.class);
        }
        return CodeBlock.of("FLAGS_$L", field.fieldName().toUpperCase());
    }

    private CodeBlock codecFlagsExprForRowKey(EntityModel model) {
        if (model.rowKeyCodecFlags().isEmpty()) {
            return CodeBlock.of("$T.of()", Map.class);
        }
        return CodeBlock.of("ROW_KEY_FLAGS");
    }

    private CodeBlock typeClassExpr(TypeMirror type) {
        if (type.getKind() == TypeKind.DECLARED) {
            DeclaredType dt = (DeclaredType) type;
            String qualifiedName = dt.asElement().toString();

            // For generic types, we need the raw type + type info
            if (!dt.getTypeArguments().isEmpty()) {
                return CodeBlock.of("$T.class", ClassName.bestGuess(qualifiedName));
            }
            return CodeBlock.of("$T.class", ClassName.bestGuess(qualifiedName));
        }
        return CodeBlock.of("$T.class", Object.class);
    }

    private TypeMirror getMultiVersionValueType(FieldModel field) {
        // For NavigableMap<Long, T>, return T
        return field.valueType();
    }

    private String generateParseExpression(TypeMirror type, String expr) {
        if (type.getKind() == TypeKind.DECLARED) {
            String name = type.toString();
            if (name.equals("java.lang.String")) return expr;
            if (name.equals("java.lang.Integer")) return "Integer.parseInt(" + expr + ")";
            if (name.equals("java.lang.Long")) return "Long.parseLong(" + expr + ")";
            if (name.equals("java.lang.Short")) return "Short.parseShort(" + expr + ")";
            if (name.equals("java.lang.Float")) return "Float.parseFloat(" + expr + ")";
            if (name.equals("java.lang.Double")) return "Double.parseDouble(" + expr + ")";
            if (name.equals("java.lang.Boolean")) return "Boolean.parseBoolean(" + expr + ")";
            if (name.equals("java.math.BigDecimal")) return "new java.math.BigDecimal(" + expr + ")";
        }
        return expr;
    }

    private String escapeRegex(String delimiter) {
        // Escape regex special chars
        return delimiter.replaceAll("([\\\\^$.|?*+()\\[\\]{}])", "\\\\$1");
    }
}
