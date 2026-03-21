package io.github.dordor12.hbase.orm.mapper;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

import java.util.Map;

/**
 * Generated mapper interface for converting entities to/from HBase data structures.
 * Implementations are generated at compile time with zero reflection.
 *
 * @param <R> the row key type
 * @param <T> the entity type
 */
public interface HBaseMapper<R, T> {

    /** Convert entity to an HBase Put. */
    Put writeAsPut(T entity);

    /** Convert an HBase Result to an entity. */
    T readFromResult(Result result);

    /** Convert entity to an HBase Result (for testing/MapReduce). */
    Result writeAsResult(T entity);

    /** Convert an HBase Put to an entity (for testing/MapReduce). */
    T readFromPut(Put put);

    /** Compose the row key bytes from an entity. */
    byte[] composeRowKey(T entity);

    /** Parse row key bytes and set the fields on the entity. */
    void parseRowKey(byte[] rowKeyBytes, T entity);

    /** Get the row key from an entity as its typed value. */
    R getRowKey(T entity);

    /** Get the HBase table name (namespace:table). */
    String getTableName();

    /** Get column families and their max version counts. */
    Map<String, Integer> getColumnFamiliesAndVersions();

    /** Get the family and qualifier bytes for a field by name. Returns [family, qualifier]. */
    byte[][] getColumn(String fieldName);
}
