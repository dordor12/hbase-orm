# hbase-orm

A compile-time annotation processor that generates type-safe HBase mappers with zero runtime reflection.

## Features

- **Zero reflection** — all mapping code generated at compile time via annotation processing
- **Type-safe** — generics for row key (`R`) and entity (`T`) types
- **Simple & composite row keys** — single-field (`@RowKey`) or multi-field (`@RowKeyComponent`)
- **Multi-version columns** — `NavigableMap<Long, T>` for time-series data
- **Inheritance** — `@MappedSuperclass` for shared fields across entities
- **Pluggable serialization** — `Codec` interface with a built-in `BestSuitCodec`
- **Async support** — `AsyncHBaseDAO` with `CompletableFuture`-based API

## Installation

### Gradle

```kotlin
dependencies {
    implementation("io.github.dordor12:hbase-orm-api:0.1.0")
    annotationProcessor("io.github.dordor12:hbase-orm-processor:0.1.0")
    annotationProcessor("io.github.dordor12:hbase-orm-api:0.1.0")
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.dordor12</groupId>
    <artifactId>hbase-orm-api</artifactId>
    <version>0.1.0</version>
</dependency>
```

Configure the annotation processor in `maven-compiler-plugin`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>io.github.dordor12</groupId>
                <artifactId>hbase-orm-processor</artifactId>
                <version>0.1.0</version>
            </path>
            <path>
                <groupId>io.github.dordor12</groupId>
                <artifactId>hbase-orm-api</artifactId>
                <version>0.1.0</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## Quick Start

### 1. Define an entity

```java
@Table(name = "citizens", namespace = "govt", families = {
    @ColumnFamily(name = "main"),
    @ColumnFamily(name = "optional", versions = 10)
})
public class Citizen {

    @RowKeyComponent(order = 0)
    private String countryCode;

    @RowKeyComponent(order = 1, delimiter = "#")
    private Integer uid;

    @Column(family = "main", qualifier = "name")
    private String name;

    @Column(family = "main", qualifier = "age")
    private Short age;

    @MultiVersion(family = "optional", qualifier = "phone_number")
    private NavigableMap<Long, Integer> phoneNumber;

    // getters and setters
}
```

### 2. Use the generated mapper

The annotation processor generates a `CitizenHBMapper` class at compile time:

```java
BestSuitCodec codec = new BestSuitCodec();
CitizenHBMapper mapper = new CitizenHBMapper(codec);

// With synchronous DAO
HBaseDAO<String, Citizen> dao = new HBaseDAO<>(connection, mapper);

Citizen citizen = new Citizen();
citizen.setCountryCode("US");
citizen.setUid(123);
citizen.setName("John");
citizen.setAge((short) 30);

// Persist
dao.persist(citizen);

// Retrieve
Citizen result = dao.get("US#123");

// Range scan
List<Citizen> range = dao.get("US#100", "US#200");

// Delete
dao.delete("US#123");
```

### 3. Async operations

```java
AsyncHBaseDAO<String, Citizen> asyncDao = new AsyncHBaseDAO<>(asyncConnection, mapper);

asyncDao.persist(citizen)
    .thenCompose(key -> asyncDao.get(key))
    .thenAccept(c -> System.out.println(c.getName()))
    .join();
```

## Annotations

| Annotation | Target | Description |
|---|---|---|
| `@Table` | Class | Marks an HBase entity. Defines table name, namespace, and column families. |
| `@RowKey` | Field | Single-field row key. The field type becomes the row key type `R`. |
| `@RowKeyComponent` | Field | Part of a composite row key. Specify `order` and optional `delimiter`. |
| `@Column` | Field | Maps a field to a single-versioned HBase column. |
| `@MultiVersion` | Field | Maps a `NavigableMap<Long, T>` field to a multi-versioned column. |
| `@ColumnFamily` | (used in `@Table`) | Defines a column family with optional `versions` count. |
| `@MappedSuperclass` | Class | Marks a superclass whose annotated fields are inherited by `@Table` subclasses. |
| `@CodecFlag` | (used in `@Column`/`@MultiVersion`) | Key-value flag to control serialization behavior. |

## Row Keys

**Simple row key** — annotate a single field with `@RowKey`. The field type (e.g., `Long`, `String`) determines the generic type `R`:

```java
@RowKey
private Long empid;
```

**Composite row key** — annotate multiple fields with `@RowKeyComponent`. Components are joined by their delimiters and the row key type is always `String`:

```java
@RowKeyComponent(order = 0)
private String countryCode;

@RowKeyComponent(order = 1, delimiter = "#")
private Integer uid;
// Row key: "US#123"
```

## Inheritance

Use `@MappedSuperclass` to share column definitions across entities:

```java
@MappedSuperclass
public abstract class AbstractRecord {
    @Column(family = "a", qualifier = "created_at")
    private LocalDateTime createdAt;
}

@Table(name = "employees", families = {@ColumnFamily(name = "a")})
public class Employee extends AbstractRecord {
    @RowKey
    private Long empid;

    @Column(family = "a", qualifier = "name")
    private String empName;
}
```

## Codec

`BestSuitCodec` uses native HBase `Bytes` utilities for primitives (`String`, `Integer`, `Long`, `Short`, `Float`, `Double`, `BigDecimal`, `Boolean`) and falls back to Jackson for complex types (including `LocalDateTime` via `JavaTimeModule`).

Force string serialization with a codec flag:

```java
@Column(family = "optional", qualifier = "pincode",
    codecFlags = {@CodecFlag(name = BestSuitCodec.SERIALIZE_AS_STRING, value = "true")})
private Integer pincode;
```

Provide a custom `ObjectMapper`:

```java
BestSuitCodec codec = new BestSuitCodec(customObjectMapper);
```

Or implement the `Codec` interface for fully custom serialization.

## DAO Methods

### HBaseDAO (synchronous)

| Category | Methods |
|---|---|
| **Read** | `get(rowKey)`, `get(rowKeys)`, `get(start, end)`, `getByPrefix(prefix)`, `get(Scan)` |
| **Write** | `persist(entity)`, `persist(entities)` |
| **Delete** | `delete(rowKey)`, `deleteEntity(entity)`, `deleteByKeys(rowKeys...)`, `deleteEntities(entities)` |
| **Atomic** | `increment(rowKey, field, amount)`, `append(Append)` |
| **Check** | `exists(rowKey)`, `exists(rowKeys...)` |

### AsyncHBaseDAO (asynchronous)

All methods return `CompletableFuture`. Supports per-call `Executor` override to offload deserialization from Netty I/O threads:

```java
asyncDao.get(rowKey, numVersions, customExecutor);
```

Batch helpers that collect all futures:

```java
CompletableFuture<List<T>> results = asyncDao.getAll(rowKeys);
CompletableFuture<List<R>> keys = asyncDao.persistAll(entities);
```

## Project Structure

```
hbase-orm/
├── hbase-orm-api/          # Annotations, Codec, DAO, Mapper interface (published)
├── hbase-orm-processor/    # Annotation processor + code generator (published)
└── hbase-orm-test/         # Example entities, unit & integration tests
```

## Building

```bash
./gradlew build
```

Run integration tests (requires Docker):

```bash
./gradlew intTest
```

## Requirements

- Java 17+
- HBase 2.4.x

## License

[Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0)
