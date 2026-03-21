package io.github.dordor12.hbase.orm.processor;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;

/**
 * Intermediate representation of a @Table-annotated entity, built during annotation processing.
 */
public record EntityModel(
        String packageName,
        String className,
        String qualifiedName,
        String tableName,
        String tableNamespace,
        Map<String, Integer> familiesAndVersions,
        Map<String, String> rowKeyCodecFlags,
        RowKeyModel rowKeyModel,
        List<FieldModel> fields
) {

    public String mapperClassName() {
        return className + "HBMapper";
    }

    public String fullTableName() {
        if ("default".equals(tableNamespace)) {
            return tableName;
        }
        return tableNamespace + ":" + tableName;
    }
}
