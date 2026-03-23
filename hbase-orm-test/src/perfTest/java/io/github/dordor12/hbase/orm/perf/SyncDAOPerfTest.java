package io.github.dordor12.hbase.orm.perf;

import io.github.dordor12.hbase.orm.perf.PerfResultReporter.BenchmarkResult;
import io.github.dordor12.hbase.orm.test.entity.Citizen;
import io.github.dordor12.hbase.orm.test.entity.Crawl;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Synchronous HBaseDAO performance tests against a real HBase instance.
 */
@Tag("perf")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SyncDAOPerfTest extends HBasePerfTestBase {

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
    void singlePutGetLatency() throws Exception {
        BenchmarkResult result = PerfResultReporter.run("sync.single.put_get", 50, 1000, () -> {
            try {
                Citizen c = new Citizen("PERF", 1, "SingleTest");
                c.setAge((short) 25);
                citizenDAO.persist(c);
                Citizen fetched = citizenDAO.get("PERF#1");
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
    void bulkPutThroughput(int batchSize) throws Exception {
        List<Citizen> batch = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            Citizen c = new Citizen("BULK", i, "Citizen-" + i);
            c.setAge((short) (20 + i % 50));
            batch.add(c);
        }

        BenchmarkResult result = PerfResultReporter.run(
                "sync.bulk_put.batch_" + batchSize, 3, 20, () -> {
                    try {
                        citizenDAO.persist(batch);
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
            Citizen c = new Citizen("BGET", i, "Citizen-" + i);
            batch.add(c);
            keys.add("BGET#" + i);
        }
        citizenDAO.persist(batch);

        BenchmarkResult result = PerfResultReporter.run(
                "sync.bulk_get.batch_" + batchSize, 3, 20, () -> {
                    try {
                        citizenDAO.get(keys);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    @Order(4)
    void prefixScanThroughput(int resultSize) throws Exception {
        // Seed data with unique prefix
        String prefix = "SCAN" + resultSize;
        List<Citizen> batch = new ArrayList<>(resultSize);
        for (int i = 0; i < resultSize; i++) {
            batch.add(new Citizen(prefix, i, "Citizen-" + i));
        }
        citizenDAO.persist(batch);

        BenchmarkResult result = PerfResultReporter.run(
                "sync.prefix_scan.size_" + resultSize, 3, 20, () -> {
                    try {
                        citizenDAO.getByPrefix(Bytes.toBytes(prefix + "#"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(result);
    }

    @Test
    @Order(5)
    void rangeScanLatency() throws Exception {
        // Seed contiguous range
        List<Citizen> batch = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            batch.add(new Citizen("RNG", i, "Citizen-" + i));
        }
        citizenDAO.persist(batch);

        BenchmarkResult result = PerfResultReporter.run("sync.range_scan", 5, 50, () -> {
            try {
                citizenDAO.get("RNG#0", "RNG#99");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(result);
    }

    @Test
    @Order(6)
    void multiVersionWriteRead() throws Exception {
        BenchmarkResult result = PerfResultReporter.run("sync.multi_version.write_read", 5, 50, () -> {
            try {
                Crawl crawl = new Crawl("perf-mv-" + System.nanoTime());
                for (int v = 0; v < 10; v++) {
                    crawl.addF1((long) (v + 1) * 1000, v * 1.1);
                }
                crawlDAO.persist(crawl);
                Crawl fetched = crawlDAO.get(crawl.getKey(), 10);
                assertNotNull(fetched);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(result);
    }
}
