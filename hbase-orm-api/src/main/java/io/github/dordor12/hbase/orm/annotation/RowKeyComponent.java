package io.github.dordor12.hbase.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a component of a composite row key.
 * Multiple fields can be annotated; they are composed/parsed in {@code order} sequence.
 * <p>
 * The composite row key type R is always {@code String}.
 * Components are joined using the specified delimiter.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RowKeyComponent {
    /** Position of this component (0-based). */
    int order();

    /** Delimiter placed AFTER this component when composing (not after the last). Defaults to "#". */
    String delimiter() default "#";
}
