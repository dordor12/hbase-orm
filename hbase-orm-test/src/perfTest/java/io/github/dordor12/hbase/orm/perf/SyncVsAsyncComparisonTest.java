package io.github.dordor12.hbase.orm.perf;

import io.github.dordor12.hbase.orm.perf.PerfResultReporter.BenchmarkResult;
import io.github.dordor12.hbase.orm.test.entity.Citizen;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Side-by-side comparison of identical operations through Sync vs Async DAOs.
 */
@Tag("perf")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SyncVsAsyncComparisonTest extends HBasePerfTestBase {

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
    void singlePutGet() {
        BenchmarkResult syncResult = PerfResultReporter.run("compare.sync.single_put_get", 50, 500, () -> {
            try {
                Citizen c = new Citizen("CSYNC", 1, "CompareSync");
                citizenDAO.persist(c);
                citizenDAO.get("CSYNC#1");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        BenchmarkResult asyncResult = PerfResultReporter.run("compare.async.single_put_get", 50, 500, () -> {
            try {
                Citizen c = new Citizen("CASYNC", 1, "CompareAsync");
                asyncCitizenDAO.persist(c).get(10, TimeUnit.SECONDS);
                asyncCitizenDAO.get("CASYNC#1").get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("=== Single Put+Get Comparison ===");
        System.out.println("  Sync:  " + syncResult);
        System.out.println("  Async: " + asyncResult);
    }

    @Test
    @Order(2)
    void bulkPut100() {
        List<Citizen> batch = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            batch.add(new Citizen("CBULK", i, "Citizen-" + i));
        }

        BenchmarkResult syncResult = PerfResultReporter.run("compare.sync.bulk_put_100", 3, 20, () -> {
            try {
                citizenDAO.persist(batch);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        BenchmarkResult asyncResult = PerfResultReporter.run("compare.async.bulk_put_100", 3, 20, () -> {
            try {
                asyncCitizenDAO.persistAll(batch).get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("=== Bulk Put 100 Comparison ===");
        System.out.println("  Sync:  " + syncResult);
        System.out.println("  Async: " + asyncResult);
    }

    @Test
    @Order(3)
    void prefixScan100() throws Exception {
        // Seed data
        List<Citizen> batch = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            batch.add(new Citizen("CPSCAN", i, "Citizen-" + i));
        }
        citizenDAO.persist(batch);

        BenchmarkResult syncResult = PerfResultReporter.run("compare.sync.prefix_scan_100", 3, 30, () -> {
            try {
                citizenDAO.getByPrefix(Bytes.toBytes("CPSCAN#"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        BenchmarkResult asyncResult = PerfResultReporter.run("compare.async.prefix_scan_100", 3, 30, () -> {
            try {
                asyncCitizenDAO.getByPrefix(Bytes.toBytes("CPSCAN#")).get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("=== Prefix Scan 100 Comparison ===");
        System.out.println("  Sync:  " + syncResult);
        System.out.println("  Async: " + asyncResult);
    }

    @Test
    @Order(4)
    void bulkGet100() throws Exception {
        // Seed data
        List<Citizen> batch = new ArrayList<>(100);
        List<String> keys = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            batch.add(new Citizen("CBGET", i, "Citizen-" + i));
            keys.add("CBGET#" + i);
        }
        citizenDAO.persist(batch);

        BenchmarkResult syncResult = PerfResultReporter.run("compare.sync.bulk_get_100", 3, 30, () -> {
            try {
                citizenDAO.get(keys);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        BenchmarkResult asyncResult = PerfResultReporter.run("compare.async.bulk_get_100", 3, 30, () -> {
            try {
                asyncCitizenDAO.getAll(keys).get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("=== Bulk Get 100 Comparison ===");
        System.out.println("  Sync:  " + syncResult);
        System.out.println("  Async: " + asyncResult);
    }
}
