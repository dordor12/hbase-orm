package io.github.dordor12.hbase.orm.codec;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hadoop.hbase.util.Bytes;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Default codec that uses native HBase serialization for primitive types
 * and falls back to Jackson JSON for complex types.
 */
public class BestSuitCodec implements Codec {

    public static final String SERIALIZE_AS_STRING = "serializeAsString";

    private final ObjectMapper objectMapper;

    public BestSuitCodec() {
        this(defaultObjectMapper());
    }

    public BestSuitCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.disable(FAIL_ON_UNKNOWN_PROPERTIES);
        om.registerModule(new JavaTimeModule());
        om.registerModule(new Jdk8Module());
        return om;
    }

    @Override
    public byte[] serialize(Object object, Map<String, String> flags) {
        if (object == null) {
            return null;
        }
        try {
            if (isSerializeAsString(flags)) {
                return Bytes.toBytes(String.valueOf(object));
            }
            if (object instanceof String s) return Bytes.toBytes(s);
            if (object instanceof Integer i) return Bytes.toBytes(i);
            if (object instanceof Short s) return Bytes.toBytes(s);
            if (object instanceof Long l) return Bytes.toBytes(l);
            if (object instanceof Float f) return Bytes.toBytes(f);
            if (object instanceof Double d) return Bytes.toBytes(d);
            if (object instanceof BigDecimal bd) return Bytes.toBytes(bd);
            if (object instanceof Boolean b) return Bytes.toBytes(b);
            // Fallback: Jackson JSON
            return objectMapper.writeValueAsBytes(object);
        } catch (Exception e) {
            throw new CodecException("Failed to serialize object of type " + object.getClass().getName(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Type type, Map<String, String> flags) {
        if (bytes == null) {
            return null;
        }
        try {
            Class<?> rawType = getRawType(type);
            if (isSerializeAsString(flags)) {
                return deserializeFromString(Bytes.toString(bytes), rawType);
            }
            if (rawType == String.class) return Bytes.toString(bytes);
            if (rawType == Integer.class) return Bytes.toInt(bytes);
            if (rawType == Short.class) return Bytes.toShort(bytes);
            if (rawType == Long.class) return Bytes.toLong(bytes);
            if (rawType == Float.class) return Bytes.toFloat(bytes);
            if (rawType == Double.class) return Bytes.toDouble(bytes);
            if (rawType == BigDecimal.class) return Bytes.toBigDecimal(bytes);
            if (rawType == Boolean.class) return Bytes.toBoolean(bytes);
            // Fallback: Jackson JSON
            JavaType javaType = objectMapper.constructType(type);
            return objectMapper.readValue(bytes, javaType);
        } catch (Exception e) {
            throw new CodecException("Failed to deserialize bytes to type " + type.getTypeName(), e);
        }
    }

    @Override
    public boolean canDeserialize(Type type) {
        try {
            Class<?> rawType = getRawType(type);
            if (rawType == String.class || rawType == Integer.class || rawType == Short.class
                    || rawType == Long.class || rawType == Float.class || rawType == Double.class
                    || rawType == BigDecimal.class || rawType == Boolean.class) {
                return true;
            }
            return objectMapper.canDeserialize(objectMapper.constructType(type));
        } catch (Exception e) {
            return false;
        }
    }

    private Object deserializeFromString(String str, Class<?> rawType) {
        if (rawType == String.class) return str;
        if (rawType == Integer.class) return Integer.valueOf(str);
        if (rawType == Short.class) return Short.valueOf(str);
        if (rawType == Long.class) return Long.valueOf(str);
        if (rawType == Float.class) return Float.valueOf(str);
        if (rawType == Double.class) return Double.valueOf(str);
        if (rawType == BigDecimal.class) return new BigDecimal(str);
        if (rawType == Boolean.class) return Boolean.valueOf(str);
        throw new CodecException("Cannot deserialize string '" + str + "' to type " + rawType.getName());
    }

    private boolean isSerializeAsString(Map<String, String> flags) {
        if (flags == null) return false;
        String val = flags.get(SERIALIZE_AS_STRING);
        return "true".equalsIgnoreCase(val);
    }

    private Class<?> getRawType(Type type) {
        if (type instanceof Class<?> c) return c;
        if (type instanceof java.lang.reflect.ParameterizedType pt) {
            return (Class<?>) pt.getRawType();
        }
        return Object.class;
    }

    public static class CodecException extends RuntimeException {
        public CodecException(String message) { super(message); }
        public CodecException(String message, Throwable cause) { super(message, cause); }
    }
}
