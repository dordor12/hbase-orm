package io.github.dordor12.hbase.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a column family within a {@link Table} annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnFamily {
    /** Column family name. */
    String name();

    /** Maximum number of versions to store. Defaults to 1. */
    int versions() default 1;
}
