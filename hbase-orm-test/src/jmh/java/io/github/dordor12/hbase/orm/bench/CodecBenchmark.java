package io.github.dordor12.hbase.orm.bench;

import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmarks for {@link BestSuitCodec} serialize/deserialize operations.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
public class CodecBenchmark {

    private BestSuitCodec codec;
    private Map<String, String> noFlags;
    private Map<String, String> stringFlags;

    // Pre-serialized bytes for deserialization benchmarks
    private byte[] stringBytes;
    private byte[] intBytes;
    private byte[] longBytes;
    private byte[] floatBytes;
    private byte[] doubleBytes;
    private byte[] shortBytes;
    private byte[] bigDecimalBytes;
    private byte[] booleanBytes;
    private byte[] localDateTimeBytes;
    private byte[] stringFlagBytes;

    @Setup
    public void setup() {
        codec = new BestSuitCodec();
        noFlags = Collections.emptyMap();
        stringFlags = Map.of(BestSuitCodec.SERIALIZE_AS_STRING, "true");

        stringBytes = codec.serialize("hello world", noFlags);
        intBytes = codec.serialize(42, noFlags);
        longBytes = codec.serialize(123456789L, noFlags);
        floatBytes = codec.serialize(3.14f, noFlags);
        doubleBytes = codec.serialize(2.718281828, noFlags);
        shortBytes = codec.serialize((short) 100, noFlags);
        bigDecimalBytes = codec.serialize(new BigDecimal("12345.6789"), noFlags);
        booleanBytes = codec.serialize(true, noFlags);
        localDateTimeBytes = codec.serialize(LocalDateTime.of(2024, 6, 15, 9, 30), noFlags);
        stringFlagBytes = codec.serialize(42, stringFlags);
    }

    // ─── Serialize ────────────────────────────────────────────────────

    @Benchmark
    public byte[] serializeString() {
        return codec.serialize("hello world", noFlags);
    }

    @Benchmark
    public byte[] serializeInteger() {
        return codec.serialize(42, noFlags);
    }

    @Benchmark
    public byte[] serializeLong() {
        return codec.serialize(123456789L, noFlags);
    }

    @Benchmark
    public byte[] serializeFloat() {
        return codec.serialize(3.14f, noFlags);
    }

    @Benchmark
    public byte[] serializeDouble() {
        return codec.serialize(2.718281828, noFlags);
    }

    @Benchmark
    public byte[] serializeShort() {
        return codec.serialize((short) 100, noFlags);
    }

    @Benchmark
    public byte[] serializeBigDecimal() {
        return codec.serialize(new BigDecimal("12345.6789"), noFlags);
    }

    @Benchmark
    public byte[] serializeBoolean() {
        return codec.serialize(true, noFlags);
    }

    @Benchmark
    public byte[] serializeLocalDateTime() {
        return codec.serialize(LocalDateTime.of(2024, 6, 15, 9, 30), noFlags);
    }

    @Benchmark
    public byte[] serializeAsString() {
        return codec.serialize(42, stringFlags);
    }

    // ─── Deserialize ──────────────────────────────────────────────────

    @Benchmark
    public Object deserializeString() {
        return codec.deserialize(stringBytes, String.class, noFlags);
    }

    @Benchmark
    public Object deserializeInteger() {
        return codec.deserialize(intBytes, Integer.class, noFlags);
    }

    @Benchmark
    public Object deserializeLong() {
        return codec.deserialize(longBytes, Long.class, noFlags);
    }

    @Benchmark
    public Object deserializeFloat() {
        return codec.deserialize(floatBytes, Float.class, noFlags);
    }

    @Benchmark
    public Object deserializeDouble() {
        return codec.deserialize(doubleBytes, Double.class, noFlags);
    }

    @Benchmark
    public Object deserializeShort() {
        return codec.deserialize(shortBytes, Short.class, noFlags);
    }

    @Benchmark
    public Object deserializeBigDecimal() {
        return codec.deserialize(bigDecimalBytes, BigDecimal.class, noFlags);
    }

    @Benchmark
    public Object deserializeBoolean() {
        return codec.deserialize(booleanBytes, Boolean.class, noFlags);
    }

    @Benchmark
    public Object deserializeLocalDateTime() {
        return codec.deserialize(localDateTimeBytes, LocalDateTime.class, noFlags);
    }

    @Benchmark
    public Object deserializeAsString() {
        return codec.deserialize(stringFlagBytes, Integer.class, stringFlags);
    }

    @Benchmark
    public boolean canDeserializeString() {
        return codec.canDeserialize(String.class);
    }
}
