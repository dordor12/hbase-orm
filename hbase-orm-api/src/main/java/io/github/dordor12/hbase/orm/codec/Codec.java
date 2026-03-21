package io.github.dordor12.hbase.orm.codec;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Interface for serializing/deserializing field values to/from byte arrays
 * for HBase storage. Unlike the old API, values are {@code Object} rather
 * than {@code Serializable}.
 */
public interface Codec {

    /**
     * Serialize an object to bytes for HBase storage.
     *
     * @param object the value to serialize (may be null)
     * @param flags  codec flags that control serialization behavior
     * @return byte array, or null if object is null
     */
    byte[] serialize(Object object, Map<String, String> flags);

    /**
     * Deserialize bytes from HBase into a typed object.
     *
     * @param bytes the byte array from HBase (may be null)
     * @param type  the target Java type
     * @param flags codec flags that control deserialization behavior
     * @return the deserialized object, or null if bytes is null
     */
    Object deserialize(byte[] bytes, Type type, Map<String, String> flags);

    /**
     * Check if this codec can deserialize the given type.
     *
     * @param type the Java type to check
     * @return true if the type is supported
     */
    boolean canDeserialize(Type type);
}
