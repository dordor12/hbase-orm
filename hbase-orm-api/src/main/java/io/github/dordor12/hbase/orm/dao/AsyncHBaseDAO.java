package io.github.dordor12.hbase.orm.dao;

import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Async DAO for HBase operations using {@link AsyncConnection} and
 * {@link AsyncTable} with {@link AdvancedScanResultConsumer}.
 * Delegates serialization/deserialization to a generated {@link HBaseMapper}.
 * <p>
 * All read methods that deserialize Results use the configured executor
 * (if provided) to offload mapping work off the Netty I/O thread.
 * Per-call overloads accept an explicit {@link Executor} to override.
 * <p>
 * Usage:
 * <pre>
 * AsyncHBaseDAO&lt;String, Citizen&gt; dao = new AsyncHBaseDAO&lt;&gt;(asyncConn, new CitizenHBMapper(codec));
 * dao.persist(citizen).join();
 * Citizen c = dao.get("US#123").join();
 * </pre>
 *
 * @param <R> the row key type
 * @param <T> the entity type
 */
public class AsyncHBaseDAO<R, T> {

    private final AsyncConnection connection;
    private final HBaseMapper<R, T> mapper;
    private final TableName tableName;
    private final Executor executor;

    public AsyncHBaseDAO(AsyncConnection connection, HBaseMapper<R, T> mapper) {
        this(connection, mapper, null);
    }

    public AsyncHBaseDAO(AsyncConnection connection, HBaseMapper<R, T> mapper, Executor executor) {
        this.connection = connection;
        this.mapper = mapper;
        this.tableName = TableName.valueOf(mapper.getTableName());
        this.executor = executor;
    }

    // ─── Read Operations ─────────────────────────────────────────────

    public CompletableFuture<T> get(R rowKey) {
        return applyMapping(getAsyncTable().get(createGet(rowKey)), this::resultToEntity);
    }

    public CompletableFuture<T> get(R rowKey, int numVersionsToFetch) throws IOException {
        Get get = createGet(rowKey);
        if (numVersionsToFetch > 1) {
            get.readVersions(numVersionsToFetch);
        }
        return applyMapping(getAsyncTable().get(get), this::resultToEntity);
    }

    public CompletableFuture<T> get(R rowKey, int numVersionsToFetch, Executor exec) throws IOException {
        Get get = createGet(rowKey);
        if (numVersionsToFetch > 1) {
            get.readVersions(numVersionsToFetch);
        }
        return applyMapping(getAsyncTable().get(get), this::resultToEntity, exec);
    }

    public List<CompletableFuture<T>> get(List<R> rowKeys) throws IOException {
        return getBatch(rowKeys, 1, null);
    }

    public List<CompletableFuture<T>> get(List<R> rowKeys, int numVersionsToFetch) throws IOException {
        return getBatch(rowKeys, numVersionsToFetch, null);
    }

    public List<CompletableFuture<T>> get(List<R> rowKeys, int numVersionsToFetch, Executor exec) throws IOException {
        return getBatch(rowKeys, numVersionsToFetch, exec);
    }

    public CompletableFuture<List<T>> getAll(List<R> rowKeys) throws IOException {
        return collectNonNull(get(rowKeys));
    }

    public CompletableFuture<List<T>> getAll(List<R> rowKeys, int numVersionsToFetch) throws IOException {
        return collectNonNull(get(rowKeys, numVersionsToFetch));
    }

    // ─── Scan Operations ─────────────────────────────────────────────

    public CompletableFuture<List<T>> scanAll(Scan scan) {
        return applyMapping(
                getAsyncTable().scanAll(scan),
                this::resultsToEntities);
    }

    public CompletableFuture<List<T>> scanAll(Scan scan, Executor exec) {
        return applyMapping(
                getAsyncTable().scanAll(scan),
                this::resultsToEntities,
                exec);
    }

    public CompletableFuture<List<T>> getByPrefix(byte[] rowPrefix) {
        Scan scan = new Scan();
        scan.setRowPrefixFilter(rowPrefix);
        return scanAll(scan);
    }

    public CompletableFuture<List<T>> getByPrefix(byte[] rowPrefix, int numVersionsToFetch) throws IOException {
        Scan scan = new Scan();
        scan.setRowPrefixFilter(rowPrefix);
        if (numVersionsToFetch > 1) {
            scan.readVersions(numVersionsToFetch);
        }
        return scanAll(scan);
    }

    public CompletableFuture<List<T>> scanRange(R startRowKey, R endRowKey) {
        return scanRange(startRowKey, true, endRowKey, false);
    }

    public CompletableFuture<List<T>> scanRange(R startRowKey, boolean startInclusive,
                                                R endRowKey, boolean endInclusive) {
        Scan scan = new Scan();
        scan.withStartRow(rowKeyToBytes(startRowKey), startInclusive);
        scan.withStopRow(rowKeyToBytes(endRowKey), endInclusive);
        return scanAll(scan);
    }

    public CompletableFuture<List<T>> scanRange(R startRowKey, boolean startInclusive,
                                                R endRowKey, boolean endInclusive,
                                                int numVersionsToFetch) throws IOException {
        Scan scan = new Scan();
        scan.withStartRow(rowKeyToBytes(startRowKey), startInclusive);
        scan.withStopRow(rowKeyToBytes(endRowKey), endInclusive);
        if (numVersionsToFetch > 1) {
            scan.readVersions(numVersionsToFetch);
        }
        return scanAll(scan);
    }

    public void scan(Scan scan, AdvancedScanResultConsumer consumer) {
        getAsyncTable().scan(scan, consumer);
    }

    public CompletableFuture<List<T>> scanStreaming(Scan scan) {
        return scanStreaming(scan, executor);
    }

    public CompletableFuture<List<T>> scanStreaming(Scan scan, Executor exec) {
        CompletableFuture<List<T>> resultFuture = new CompletableFuture<>();
        List<T> entities = Collections.synchronizedList(new ArrayList<>());

        getAsyncTable().scan(scan, new AdvancedScanResultConsumer() {
            @Override
            public void onNext(Result[] results, ScanController controller) {
                Runnable mapTask = () -> {
                    for (Result r : results) {
                        if (r != null && !r.isEmpty()) {
                            entities.add(mapper.readFromResult(r));
                        }
                    }
                };
                if (exec != null) {
                    exec.execute(mapTask);
                } else {
                    mapTask.run();
                }
            }

            @Override
            public void onError(Throwable error) {
                resultFuture.completeExceptionally(error);
            }

            @Override
            public void onComplete() {
                resultFuture.complete(entities);
            }
        });

        return resultFuture;
    }

    // ─── Write Operations ────────────────────────────────────────────

    public CompletableFuture<R> persist(T entity) {
        Put put = mapper.writeAsPut(entity);
        R rowKey = mapper.getRowKey(entity);
        return getAsyncTable().put(put).thenApply(v -> rowKey);
    }

    public List<CompletableFuture<R>> persist(List<T> entities) {
        AsyncTable<AdvancedScanResultConsumer> table = getAsyncTable();
        List<CompletableFuture<R>> futures = new ArrayList<>(entities.size());
        for (T entity : entities) {
            Put put = mapper.writeAsPut(entity);
            R rowKey = mapper.getRowKey(entity);
            futures.add(table.put(put).thenApply(v -> rowKey));
        }
        return futures;
    }

    public CompletableFuture<List<R>> persistAll(List<T> entities) {
        List<CompletableFuture<R>> futures = persist(entities);
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<R> keys = new ArrayList<>(futures.size());
                    for (CompletableFuture<R> f : futures) {
                        keys.add(f.join());
                    }
                    return keys;
                });
    }

    // ─── Delete Operations ───────────────────────────────────────────

    public CompletableFuture<Void> delete(R rowKey) {
        Delete delete = new Delete(rowKeyToBytes(rowKey));
        return getAsyncTable().delete(delete);
    }

    @SuppressWarnings("unchecked")
    public List<CompletableFuture<Void>> deleteByKeys(R... rowKeys) {
        AsyncTable<AdvancedScanResultConsumer> table = getAsyncTable();
        List<Delete> deletes = new ArrayList<>(rowKeys.length);
        for (R rk : rowKeys) {
            deletes.add(new Delete(rowKeyToBytes(rk)));
        }
        return table.delete(deletes);
    }

    public CompletableFuture<Void> deleteEntity(T entity) {
        return delete(mapper.getRowKey(entity));
    }

    // ─── Atomic Operations ───────────────────────────────────────────

    public CompletableFuture<Long> increment(R rowKey, String fieldName, long amount) {
        byte[][] col = mapper.getColumn(fieldName);
        Increment inc = new Increment(rowKeyToBytes(rowKey));
        inc.addColumn(col[0], col[1], amount);
        return applyMapping(
                getAsyncTable().increment(inc),
                result -> Bytes.toLong(result.getValue(col[0], col[1])));
    }

    public CompletableFuture<T> increment(Increment increment) {
        return applyMapping(
                getAsyncTable().increment(increment),
                this::resultToEntity);
    }

    public CompletableFuture<T> append(Append append) {
        return applyMapping(
                getAsyncTable().append(append),
                this::resultToEntity);
    }

    // ─── Existence Check ─────────────────────────────────────────────

    public CompletableFuture<Boolean> exists(R rowKey) {
        return getAsyncTable().exists(createGet(rowKey));
    }

    @SuppressWarnings("unchecked")
    public List<CompletableFuture<Boolean>> exists(R... rowKeys) {
        AsyncTable<AdvancedScanResultConsumer> table = getAsyncTable();
        List<Get> gets = new ArrayList<>(rowKeys.length);
        for (R rk : rowKeys) {
            gets.add(createGet(rk));
        }
        return table.exists(gets);
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    public AsyncTable<AdvancedScanResultConsumer> getAsyncTable() {
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

    private T resultToEntity(Result result) {
        if (result == null || result.isEmpty()) {
            return null;
        }
        return mapper.readFromResult(result);
    }

    private List<T> resultsToEntities(List<Result> results) {
        List<T> entities = new ArrayList<>(results.size());
        for (Result r : results) {
            if (r != null && !r.isEmpty()) {
                entities.add(mapper.readFromResult(r));
            }
        }
        return entities;
    }

    private List<CompletableFuture<T>> getBatch(List<R> rowKeys, int numVersionsToFetch,
                                                Executor exec) throws IOException {
        AsyncTable<AdvancedScanResultConsumer> table = getAsyncTable();
        List<Get> gets = new ArrayList<>(rowKeys.size());
        for (R rk : rowKeys) {
            Get g = createGet(rk);
            if (numVersionsToFetch > 1) {
                g.readVersions(numVersionsToFetch);
            }
            gets.add(g);
        }
        List<CompletableFuture<Result>> rawFutures = table.get(gets);
        List<CompletableFuture<T>> result = new ArrayList<>(rawFutures.size());
        for (CompletableFuture<Result> rf : rawFutures) {
            if (exec != null) {
                result.add(applyMapping(rf, this::resultToEntity, exec));
            } else {
                result.add(applyMapping(rf, this::resultToEntity));
            }
        }
        return result;
    }

    private <V> CompletableFuture<List<V>> collectNonNull(List<CompletableFuture<V>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<V> entities = new ArrayList<>(futures.size());
                    for (CompletableFuture<V> f : futures) {
                        V entity = f.join();
                        if (entity != null) {
                            entities.add(entity);
                        }
                    }
                    return entities;
                });
    }

    private <I, O> CompletableFuture<O> applyMapping(CompletableFuture<I> future, Function<I, O> fn) {
        if (executor != null) {
            return future.thenApplyAsync(fn, executor);
        }
        return future.thenApply(fn);
    }

    private <I, O> CompletableFuture<O> applyMapping(CompletableFuture<I> future, Function<I, O> fn, Executor exec) {
        if (exec != null) {
            return future.thenApplyAsync(fn, exec);
        }
        return future.thenApply(fn);
    }

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
