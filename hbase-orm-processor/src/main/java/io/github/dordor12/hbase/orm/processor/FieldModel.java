package io.github.dordor12.hbase.orm.processor;

import javax.lang.model.type.TypeMirror;
import java.util.Map;

/**
 * Intermediate representation of a @Column or @MultiVersion annotated field.
 */
public record FieldModel(
        String fieldName,
        String family,
        String qualifier,
        TypeMirror fieldType,
        TypeMirror valueType,
        boolean multiVersioned,
        Map<String, String> codecFlags,
        String getterName,
        String setterName,
        boolean needsAccessor
) {
}
