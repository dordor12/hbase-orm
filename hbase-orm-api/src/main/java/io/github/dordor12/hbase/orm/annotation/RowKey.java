package io.github.dordor12.hbase.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a single field as the row key. The field type determines the row key type R.
 * Use this for simple (non-composite) row keys.
 * <p>
 * For composite row keys, use {@link RowKeyComponent} on multiple fields instead.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RowKey {
}
