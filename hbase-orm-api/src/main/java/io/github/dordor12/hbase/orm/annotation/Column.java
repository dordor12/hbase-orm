package io.github.dordor12.hbase.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a field to a single-versioned HBase column.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /** Column family name. */
    String family();

    /** Column qualifier. */
    String qualifier();

    /** Codec flags for this field's serialization. */
    CodecFlag[] codecFlags() default {};
}
