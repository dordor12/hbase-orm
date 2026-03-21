package io.github.dordor12.hbase.orm.dao;

import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

/**
 * Concrete DAO for HBase operations. Delegates serialization/deserialization
 * to a generated {@link HBaseMapper}.
 * <p>
 * Usage:
 * <pre>
 * HBaseDAO&lt;String, Citizen&gt; dao = new HBaseDAO&lt;&gt;(connection, new CitizenHBMapper(codec));
 * dao.persist(citizen);
 * Citizen c = dao.get("US#123");
 * </pre>
 *
 * @param <R> the row key type
 * @param <T> the entity type
 */
public class HBaseDAO<R, T> {

    private final Connection connection;
    private final HBaseMapper<R, T> mapper;
    private final TableName tableName;

    public HBaseDAO(Connection connection, HBaseMapper<R, T> mapper) {
        this.connection = connection;
        this.mapper = mapper;
        this.tableName = TableName.valueOf(mapper.getTableName());
    }

    // ─── Read Operations ─────────────────────────────────────────────

    public T get(R rowKey) throws IOException {
        return get(rowKey, 1);
    }

    public T get(R rowKey, int numVersionsToFetch) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            Get get = createGet(rowKey);
            if (numVersionsToFetch > 1) {
                get.readVersions(numVersionsToFetch);
            }
            Result result = table.get(get);
            if (result == null || result.isEmpty()) {
                return null;
            }
            return mapper.readFromResult(result);
        }
    }

    public List<T> get(List<R> rowKeys) throws IOException {
        return get(rowKeys, 1);
    }

    public List<T> get(List<R> rowKeys, int numVersionsToFetch) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            List<Get> gets = new ArrayList<>(rowKeys.size());
            for (R rk : rowKeys) {
                Get g = createGet(rk);
                if (numVersionsToFetch > 1) {
                    g.readVersions(numVersionsToFetch);
                }
                gets.add(g);
            }
            Result[] results = table.get(gets);
            List<T> entities = new ArrayList<>(results.length);
            for (Result r : results) {
                if (r != null && !r.isEmpty()) {
                    entities.add(mapper.readFromResult(r));
                }
            }
            return entities;
        }
    }

    public List<T> get(R startRowKey, R endRowKey) throws IOException {
        return get(startRowKey, true, endRowKey, false, 1);
    }

    public List<T> get(R startRowKey, boolean startInclusive,
                       R endRowKey, boolean endInclusive,
                       int numVersionsToFetch) throws IOException {
        Scan scan = new Scan();
        scan.withStartRow(rowKeyToBytes(startRowKey), startInclusive);
        scan.withStopRow(rowKeyToBytes(endRowKey), endInclusive);
        if (numVersionsToFetch > 1) {
            scan.readVersions(numVersionsToFetch);
        }
        return get(scan);
    }

    public List<T> get(Scan scan) throws IOException {
        List<T> entities = new ArrayList<>();
        try (Table table = connection.getTable(tableName);
             ResultScanner scanner = table.getScanner(scan)) {
            for (Result result : scanner) {
                if (result != null && !result.isEmpty()) {
                    entities.add(mapper.readFromResult(result));
                }
            }
        }
        return entities;
    }

    public List<T> getByPrefix(byte[] rowPrefix) throws IOException {
        return getByPrefix(rowPrefix, 1);
    }

    public List<T> getByPrefix(byte[] rowPrefix, int numVersionsToFetch) throws IOException {
        Scan scan = new Scan();
        scan.setRowPrefixFilter(rowPrefix);
        if (numVersionsToFetch > 1) {
            scan.readVersions(numVersionsToFetch);
        }
        return get(scan);
    }

    public T getOnGet(Get get) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            Result result = table.get(get);
            if (result == null || result.isEmpty()) {
                return null;
            }
            return mapper.readFromResult(result);
        }
    }

    public List<T> getOnGets(List<Get> gets) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            Result[] results = table.get(gets);
            List<T> entities = new ArrayList<>(results.length);
            for (Result r : results) {
                if (r != null && !r.isEmpty()) {
                    entities.add(mapper.readFromResult(r));
                }
            }
            return entities;
        }
    }

    // ─── Write Operations ────────────────────────────────────────────

    public R persist(T entity) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            Put put = mapper.writeAsPut(entity);
            table.put(put);
            return mapper.getRowKey(entity);
        }
    }

    public List<R> persist(List<T> entities) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            List<Put> puts = new ArrayList<>(entities.size());
            List<R> rowKeys = new ArrayList<>(entities.size());
            for (T entity : entities) {
                puts.add(mapper.writeAsPut(entity));
                rowKeys.add(mapper.getRowKey(entity));
            }
            table.put(puts);
            return rowKeys;
        }
    }

    // ─── Delete Operations ───────────────────────────────────────────

    public void delete(R rowKey) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            Delete delete = new Delete(rowKeyToBytes(rowKey));
            table.delete(delete);
        }
    }

    public void deleteEntity(T entity) throws IOException {
        delete(mapper.getRowKey(entity));
    }

    @SuppressWarnings("unchecked")
    public void deleteByKeys(R... rowKeys) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            List<Delete> deletes = new ArrayList<>(rowKeys.length);
            for (R rk : rowKeys) {
                deletes.add(new Delete(rowKeyToBytes(rk)));
            }
            table.delete(deletes);
        }
    }

    public void deleteEntities(List<T> entities) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            List<Delete> deletes = new ArrayList<>(entities.size());
            for (T entity : entities) {
                deletes.add(new Delete(mapper.composeRowKey(entity)));
            }
            table.delete(deletes);
        }
    }

    // ─── Atomic Operations ───────────────────────────────────────────

    public long increment(R rowKey, String fieldName, long amount) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            byte[][] col = mapper.getColumn(fieldName);
            return table.incrementColumnValue(rowKeyToBytes(rowKey), col[0], col[1], amount);
        }
    }

    public T increment(Increment increment) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            Result result = table.increment(increment);
            return mapper.readFromResult(result);
        }
    }

    public T append(Append append) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            Result result = table.append(append);
            return mapper.readFromResult(result);
        }
    }

    // ─── Existence Check ─────────────────────────────────────────────

    public boolean exists(R rowKey) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            Get get = createGet(rowKey);
            return table.exists(get);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean[] exists(R... rowKeys) throws IOException {
        try (Table table = connection.getTable(tableName)) {
            List<Get> gets = new ArrayList<>(rowKeys.length);
            for (R rk : rowKeys) {
                gets.add(createGet(rk));
            }
            return table.exists(gets);
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    public Table getHBaseTable() throws IOException {
        return connection.getTable(tableName);
    }

    public Get createGet(R rowKey) {
        return new Get(rowKeyToBytes(rowKey));
    }

    public Increment createIncrement(R rowKey) {
        return new Increment(rowKeyToBytes(rowKey));
    }

    public Append createAppend(R rowKey) {
        return new Append(rowKeyToBytes(rowKey));
    }

    public String getTableName() {
        return mapper.getTableName();
    }

    public Map<String, Integer> getColumnFamiliesAndVersions() {
        return mapper.getColumnFamiliesAndVersions();
    }

    public HBaseMapper<R, T> getMapper() {
        return mapper;
    }

    // ─── Internal ────────────────────────────────────────────────────

    private byte[] rowKeyToBytes(R rowKey) {
        if (rowKey instanceof String s) return Bytes.toBytes(s);
        if (rowKey instanceof Long l) return Bytes.toBytes(l);
        if (rowKey instanceof Integer i) return Bytes.toBytes(i);
        if (rowKey instanceof Short s) return Bytes.toBytes(s);
        if (rowKey instanceof Float f) return Bytes.toBytes(f);
        if (rowKey instanceof Double d) return Bytes.toBytes(d);
        if (rowKey instanceof byte[] b) return b;
        throw new IllegalArgumentException("Unsupported row key type: " + rowKey.getClass().getName());
    }

}
