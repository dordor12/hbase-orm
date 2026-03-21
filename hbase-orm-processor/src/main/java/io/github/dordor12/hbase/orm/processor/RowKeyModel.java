package io.github.dordor12.hbase.orm.processor;

import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Intermediate representation of the row key strategy.
 */
public sealed interface RowKeyModel {

    /**
     * Simple row key: a single @RowKey field.
     */
    record Simple(
            String fieldName,
            TypeMirror fieldType,
            String getterName,
            String setterName
    ) implements RowKeyModel {}

    /**
     * Composite row key: multiple @RowKeyComponent fields joined by delimiters.
     * The row key type R is always String for composite keys.
     */
    record Composite(
            List<Component> components
    ) implements RowKeyModel {}

    record Component(
            int order,
            String fieldName,
            TypeMirror fieldType,
            String delimiter,
            String getterName,
            String setterName
    ) {}
}
