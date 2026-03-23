package io.github.dordor12.hbase.orm.perf;

import io.github.dordor12.hbase.orm.dao.AsyncHBaseDAO;
import io.github.dordor12.hbase.orm.perf.PerfResultReporter.BenchmarkResult;
import io.github.dordor12.hbase.orm.test.entity.Citizen;
import io.github.dordor12.hbase.orm.test.entity.CitizenHBMapper;
import io.github.dordor12.hbase.orm.test.entity.Crawl;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Async HBaseDAO performance tests against a real HBase instance.
 */
@Tag("perf")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AsyncDAOPerfTest extends HBasePerfTestBase {

    @BeforeAll
    static void init() {
        PerfResultReporter.clear();
    }

    @AfterAll
    static void report() throws Exception {
        PerfResultReporter.writeReport(Path.of(System.getProperty("project.dir")));
    }

    @Test
    @Order(1)
    void singlePutGetLatency() {
        BenchmarkResult result = PerfResultReporter.run("async.single.put_get", 50, 1000, () -> {
            try {
                Citizen c = new Citizen("APERF", 1, "AsyncSingle");
                c.setAge((short) 25);
                asyncCitizenDAO.persist(c).get(10, TimeUnit.SECONDS);
                Citizen fetched = asyncCitizenDAO.get("APERF#1").get(10, TimeUnit.SECONDS);
                assertNotNull(fetched);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    @Order(2)
    void bulkPutThroughput(int batchSize) {
        List<Citizen> batch = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            Citizen c = new Citizen("ABULK", i, "Citizen-" + i);
            c.setAge((short) (20 + i % 50));
            batch.add(c);
        }

        BenchmarkResult result = PerfResultReporter.run(
                "async.bulk_put.batch_" + batchSize, 3, 20, () -> {
                    try {
                        asyncCitizenDAO.persistAll(batch).get(30, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    @Order(3)
    void bulkGetThroughput(int batchSize) throws Exception {
        // Seed data
        List<Citizen> batch = new ArrayList<>(batchSize);
        List<String> keys = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            Citizen c = new Citizen("ABGET", i, "Citizen-" + i);
            batch.add(c);
            keys.add("ABGET#" + i);
        }
        asyncCitizenDAO.persistAll(batch).get(30, TimeUnit.SECONDS);

        BenchmarkResult result = PerfResultReporter.run(
                "async.bulk_get.batch_" + batchSize, 3, 20, () -> {
                    try {
                        asyncCitizenDAO.getAll(keys).get(30, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(result);
    }

    @Test
    @Order(4)
    void concurrentBulkPut() {
        BenchmarkResult result = PerfResultReporter.run("async.concurrent_bulk_put.1000", 2, 10, () -> {
            try {
                List<CompletableFuture<String>> futures = new ArrayList<>(1000);
                for (int i = 0; i < 1000; i++) {
                    Citizen c = new Citizen("CONC", i, "Concurrent-" + i);
                    futures.add(asyncCitizenDAO.persist(c));
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    @Order(5)
    void prefixScanThroughput(int resultSize) throws Exception {
        // Seed data
        String prefix = "ASCAN" + resultSize;
        List<Citizen> batch = new ArrayList<>(resultSize);
        for (int i = 0; i < resultSize; i++) {
            batch.add(new Citizen(prefix, i, "Citizen-" + i));
        }
        asyncCitizenDAO.persistAll(batch).get(30, TimeUnit.SECONDS);

        BenchmarkResult result = PerfResultReporter.run(
                "async.prefix_scan.size_" + resultSize, 3, 20, () -> {
                    try {
                        asyncCitizenDAO.getByPrefix(Bytes.toBytes(prefix + "#")).get(30, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(result);
    }

    @Test
    @Order(6)
    void executorComparison() {
        // Seed data for scan
        try {
            List<Citizen> batch = new ArrayList<>(100);
            for (int i = 0; i < 100; i++) {
                batch.add(new Citizen("EXEC", i, "Citizen-" + i));
            }
            asyncCitizenDAO.persistAll(batch).get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Scan scan = new Scan();
        scan.setRowPrefixFilter(Bytes.toBytes("EXEC#"));

        // Default executor (null — inline)
        AsyncHBaseDAO<String, Citizen> defaultDAO = asyncCitizenDAO;
        BenchmarkResult defaultResult = PerfResultReporter.run("async.executor.default", 3, 30, () -> {
            try {
                defaultDAO.scanAll(scan).get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(defaultResult);

        // Fixed-4 executor
        ExecutorService fixed4 = Executors.newFixedThreadPool(4);
        try {
            AsyncHBaseDAO<String, Citizen> fixed4DAO = new AsyncHBaseDAO<>(asyncConnection, new CitizenHBMapper(codec), fixed4);
            BenchmarkResult fixed4Result = PerfResultReporter.run("async.executor.fixed_4", 3, 30, () -> {
                try {
                    fixed4DAO.scanAll(scan).get(30, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println(fixed4Result);
        } finally {
            fixed4.shutdown();
        }

        // Single-thread executor
        ExecutorService single = Executors.newSingleThreadExecutor();
        try {
            AsyncHBaseDAO<String, Citizen> singleDAO = new AsyncHBaseDAO<>(asyncConnection, new CitizenHBMapper(codec), single);
            BenchmarkResult singleResult = PerfResultReporter.run("async.executor.single_thread", 3, 30, () -> {
                try {
                    singleDAO.scanAll(scan).get(30, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println(singleResult);
        } finally {
            single.shutdown();
        }
    }

    @Test
    @Order(7)
    void multiVersionWriteRead() {
        BenchmarkResult result = PerfResultReporter.run("async.multi_version.write_read", 5, 50, () -> {
            try {
                Crawl crawl = new Crawl("async-mv-" + System.nanoTime());
                for (int v = 0; v < 10; v++) {
                    crawl.addF1((long) (v + 1) * 1000, v * 1.1);
                }
                asyncCrawlDAO.persist(crawl).get(10, TimeUnit.SECONDS);
                Crawl fetched = asyncCrawlDAO.get(crawl.getKey(), 10).get(10, TimeUnit.SECONDS);
                assertNotNull(fetched);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(result);
    }
}
