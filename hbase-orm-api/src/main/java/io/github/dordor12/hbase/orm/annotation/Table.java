package io.github.dordor12.hbase.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a class to an HBase table. Each annotated class will have
 * a mapper generated at compile time.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /** HBase namespace. Defaults to "default". */
    String namespace() default "default";

    /** HBase table name (required). */
    String name();

    /** Column family definitions. */
    ColumnFamily[] families();

    /** Codec flags for row key serialization. */
    CodecFlag[] rowKeyCodecFlags() default {};
}
