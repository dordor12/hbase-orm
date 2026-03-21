package io.github.dordor12.hbase.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a field to a multi-versioned HBase column.
 * The field type must be {@code NavigableMap<Long, T>} where T is the value type.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiVersion {
    /** Column family name. */
    String family();

    /** Column qualifier. */
    String qualifier();

    /** Codec flags for this field's serialization. */
    CodecFlag[] codecFlags() default {};
}
