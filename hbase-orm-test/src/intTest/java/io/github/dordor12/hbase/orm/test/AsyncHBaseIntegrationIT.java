package io.github.dordor12.hbase.orm.test;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.dao.AsyncHBaseDAO;
import io.github.dordor12.hbase.orm.mapper.HBaseMapper;
import io.github.dordor12.hbase.orm.test.entity.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.math.BigDecimal;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Async integration tests that verify {@link AsyncHBaseDAO} against a real HBase instance
 * running in a Docker container via Testcontainers.
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AsyncHBaseIntegrationIT {

    private static final int ZK_PORT = 2181;
    private static final int MASTER_PORT = 16000;
    private static final int RS_PORT = 16020;
    private static final int MASTER_UI_PORT = 16010;

    private static GenericContainer<?> hbaseContainer;
    private static Connection syncConnection;
    private static AsyncConnection asyncConnection;

    private static AsyncHBaseDAO<String, Citizen> citizenDAO;
    private static AsyncHBaseDAO<Long, Employee> employeeDAO;
    private static AsyncHBaseDAO<String, Crawl> crawlDAO;

    @BeforeAll
    @SuppressWarnings("resource")
    static void setUp() throws Exception {
        hbaseContainer = new GenericContainer<>("hbase-test:2.4.18")
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withHostName("localhost");
                    cmd.getHostConfig()
                            .withPortBindings(
                                    new PortBinding(Ports.Binding.bindPort(ZK_PORT), new ExposedPort(ZK_PORT)),
                                    new PortBinding(Ports.Binding.bindPort(MASTER_PORT), new ExposedPort(MASTER_PORT)),
                                    new PortBinding(Ports.Binding.bindPort(RS_PORT), new ExposedPort(RS_PORT)),
                                    new PortBinding(Ports.Binding.bindPort(MASTER_UI_PORT), new ExposedPort(MASTER_UI_PORT))
                            );
                })
                .waitingFor(Wait.forLogMessage(".*Master has completed initialization.*", 1))
                .withStartupTimeout(Duration.ofMinutes(3));

        hbaseContainer.start();
        waitForZookeeper("localhost", ZK_PORT, 60);

        Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, "localhost");
        conf.setInt(HConstants.ZOOKEEPER_CLIENT_PORT, ZK_PORT);
        conf.set(HConstants.ZOOKEEPER_ZNODE_PARENT, "/hbase");

        // Sync connection for Admin (table setup)
        syncConnection = ConnectionFactory.createConnection(conf);
        // Async connection for DAO
        asyncConnection = ConnectionFactory.createAsyncConnection(conf).get(30, TimeUnit.SECONDS);

        createNamespacesAndTables();

        BestSuitCodec codec = new BestSuitCodec();
        citizenDAO = new AsyncHBaseDAO<>(asyncConnection, new CitizenHBMapper(codec));
        employeeDAO = new AsyncHBaseDAO<>(asyncConnection, new EmployeeHBMapper(codec));
        crawlDAO = new AsyncHBaseDAO<>(asyncConnection, new CrawlHBMapper(codec));
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (asyncConnection != null) asyncConnection.close();
        if (syncConnection != null) syncConnection.close();
        if (hbaseContainer != null) hbaseContainer.stop();
    }

    // ─── Table Setup ─────────────────────────────────────────────────

    private static void createNamespacesAndTables() throws Exception {
        try (Admin admin = syncConnection.getAdmin()) {
            try {
                admin.createNamespace(NamespaceDescriptor.create("govt").build());
            } catch (Exception e) {
                // namespace may already exist
            }

            createTableFromMapper(admin, new CitizenHBMapper(new BestSuitCodec()));
            createTableFromMapper(admin, new EmployeeHBMapper(new BestSuitCodec()));
            createTableFromMapper(admin, new CrawlHBMapper(new BestSuitCodec()));
        }
    }

    private static void createTableFromMapper(Admin admin, HBaseMapper<?, ?> mapper) throws Exception {
        TableName tableName = TableName.valueOf(mapper.getTableName());
        if (admin.tableExists(tableName)) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        }

        TableDescriptorBuilder tableBuilder = TableDescriptorBuilder.newBuilder(tableName);
        for (Map.Entry<String, Integer> entry : mapper.getColumnFamiliesAndVersions().entrySet()) {
            ColumnFamilyDescriptorBuilder cfBuilder =
                    ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(entry.getKey()));
            cfBuilder.setMaxVersions(entry.getValue());
            tableBuilder.setColumnFamily(cfBuilder.build());
        }
        admin.createTable(tableBuilder.build());
    }

    // ─── Test 1: Persist + Get Roundtrip ─────────────────────────────

    @Test
    @Order(1)
    void citizenPersistAndGet() throws Exception {
        Citizen citizen = new Citizen("US", 42, "Alice");
        citizen.setAge((short) 30);
        citizen.setSal(100000);
        citizen.setIsPassportHolder(true);
        citizen.setF1(1.5f);
        citizen.setF2(2.5);
        citizen.setF3(999L);
        citizen.setF4(new BigDecimal("12345.67"));
        citizen.setPincode(90210);

        String rowKey = citizenDAO.persist(citizen).get(10, TimeUnit.SECONDS);
        assertEquals("US#42", rowKey);

        Citizen fetched = citizenDAO.get("US#42").get(10, TimeUnit.SECONDS);
        assertNotNull(fetched);
        assertEquals("US", fetched.getCountryCode());
        assertEquals(Integer.valueOf(42), fetched.getUid());
        assertEquals("Alice", fetched.getName());
        assertEquals(Short.valueOf((short) 30), fetched.getAge());
        assertEquals(Integer.valueOf(100000), fetched.getSal());
        assertTrue(fetched.getIsPassportHolder());
        assertEquals(1.5f, fetched.getF1());
        assertEquals(2.5, fetched.getF2());
        assertEquals(999L, fetched.getF3());
        assertEquals(new BigDecimal("12345.67"), fetched.getF4());
        assertEquals(Integer.valueOf(90210), fetched.getPincode());
    }

    // ─── Test 2: Bulk Persist + GetAll ───────────────────────────────

    @Test
    @Order(2)
    void citizenBulkPersistAndGetAll() throws Exception {
        List<Citizen> citizens = List.of(
                new Citizen("US", 100, "Bob"),
                new Citizen("US", 101, "Carol"),
                new Citizen("GB", 200, "Dave")
        );

        List<String> keys = citizenDAO.persistAll(citizens).get(10, TimeUnit.SECONDS);
        assertEquals(3, keys.size());
        assertEquals("US#100", keys.get(0));

        List<Citizen> fetched = citizenDAO.getAll(List.of("US#100", "US#101", "GB#200"))
                .get(10, TimeUnit.SECONDS);
        assertEquals(3, fetched.size());
    }

    // ─── Test 3: Exists ──────────────────────────────────────────────

    @Test
    @Order(3)
    void citizenExists() throws Exception {
        Boolean existsPositive = citizenDAO.exists("US#42").get(10, TimeUnit.SECONDS);
        assertTrue(existsPositive);

        Boolean existsNegative = citizenDAO.exists("XX#999").get(10, TimeUnit.SECONDS);
        assertFalse(existsNegative);
    }

    // ─── Test 4: Delete Lifecycle ────────────────────────────────────

    @Test
    @Order(4)
    void citizenDeleteLifecycle() throws Exception {
        Citizen temp = new Citizen("DE", 1, "Temp");
        citizenDAO.persist(temp).get(10, TimeUnit.SECONDS);
        assertTrue(citizenDAO.exists("DE#1").get(10, TimeUnit.SECONDS));

        citizenDAO.delete("DE#1").get(10, TimeUnit.SECONDS);
        assertFalse(citizenDAO.exists("DE#1").get(10, TimeUnit.SECONDS));
    }

    // ─── Test 5: ScanAll with Prefix ─────────────────────────────────

    @Test
    @Order(5)
    void citizenScanAllWithPrefix() throws Exception {
        Scan scan = new Scan();
        scan.setRowPrefixFilter(Bytes.toBytes("US#"));

        List<Citizen> usCitizens = citizenDAO.scanAll(scan).get(10, TimeUnit.SECONDS);
        assertTrue(usCitizens.size() >= 3, "Expected at least 3 US citizens, got " + usCitizens.size());
        for (Citizen c : usCitizens) {
            assertEquals("US", c.getCountryCode());
        }
    }

    // ─── Test 6: GetByPrefix ─────────────────────────────────────────

    @Test
    @Order(6)
    void citizenGetByPrefix() throws Exception {
        List<Citizen> usCitizens = citizenDAO.getByPrefix(Bytes.toBytes("US#"))
                .get(10, TimeUnit.SECONDS);
        assertTrue(usCitizens.size() >= 3);
        for (Citizen c : usCitizens) {
            assertEquals("US", c.getCountryCode());
        }
    }

    // ─── Test 7: ScanStreaming ────────────────────────────────────────

    @Test
    @Order(7)
    void citizenScanStreaming() throws Exception {
        Scan scan = new Scan();
        scan.setRowPrefixFilter(Bytes.toBytes("US#"));

        List<Citizen> usCitizens = citizenDAO.scanStreaming(scan).get(10, TimeUnit.SECONDS);
        assertTrue(usCitizens.size() >= 3, "Expected at least 3 US citizens via streaming");
        for (Citizen c : usCitizens) {
            assertEquals("US", c.getCountryCode());
        }
    }

    // ─── Test 8: Raw Scan with AdvancedScanResultConsumer ────────────

    @Test
    @Order(8)
    void citizenRawScanWithConsumer() throws Exception {
        Scan scan = new Scan();
        scan.setRowPrefixFilter(Bytes.toBytes("US#"));

        CompletableFuture<List<Citizen>> resultFuture = new CompletableFuture<>();
        List<Citizen> collected = Collections.synchronizedList(new ArrayList<>());
        HBaseMapper<String, Citizen> mapper = citizenDAO.getMapper();

        citizenDAO.scan(scan, new AdvancedScanResultConsumer() {
            @Override
            public void onNext(Result[] results, ScanController controller) {
                for (Result r : results) {
                    if (r != null && !r.isEmpty()) {
                        collected.add(mapper.readFromResult(r));
                    }
                }
            }

            @Override
            public void onError(Throwable error) {
                resultFuture.completeExceptionally(error);
            }

            @Override
            public void onComplete() {
                resultFuture.complete(collected);
            }
        });

        List<Citizen> result = resultFuture.get(10, TimeUnit.SECONDS);
        assertTrue(result.size() >= 3, "Expected at least 3 US citizens via raw consumer");
    }

    // ─── Test 9: ScanAll with Custom Executor ────────────────────────

    @Test
    @Order(9)
    void citizenScanAllWithExecutor() throws Exception {
        ExecutorService customExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "custom-deser-thread");
            t.setDaemon(true);
            return t;
        });
        try {
            Scan scan = new Scan();
            scan.setRowPrefixFilter(Bytes.toBytes("US#"));

            List<Citizen> usCitizens = citizenDAO.scanAll(scan, customExecutor)
                    .get(10, TimeUnit.SECONDS);
            assertTrue(usCitizens.size() >= 3);
        } finally {
            customExecutor.shutdown();
        }
    }

    // ─── Test 10: Atomic Increment ───────────────────────────────────

    @Test
    @Order(10)
    void citizenAtomicIncrement() throws Exception {
        Citizen citizen = new Citizen("US", 500, "Counter");
        citizen.setF3(0L);
        citizenDAO.persist(citizen).get(10, TimeUnit.SECONDS);

        long val1 = citizenDAO.increment("US#500", "f3", 10).get(10, TimeUnit.SECONDS);
        assertEquals(10, val1);

        long val2 = citizenDAO.increment("US#500", "f3", 5).get(10, TimeUnit.SECONDS);
        assertEquals(15, val2);
    }

    // ─── Test 11: Employee (MappedSuperclass) ────────────────────────

    @Test
    @Order(11)
    void employeePersistAndGet() throws Exception {
        Employee emp = new Employee(1L, "Engineer");
        emp.setReporteeCount((short) 5);
        emp.setCreatedAt(LocalDateTime.of(2024, 6, 15, 9, 30));

        Long rowKey = employeeDAO.persist(emp).get(10, TimeUnit.SECONDS);
        assertEquals(Long.valueOf(1), rowKey);

        Employee fetched = employeeDAO.get(1L).get(10, TimeUnit.SECONDS);
        assertNotNull(fetched);
        assertEquals(Long.valueOf(1), fetched.getEmpid());
        assertEquals("Engineer", fetched.getEmpName());
        assertEquals(Short.valueOf((short) 5), fetched.getReporteeCount());
        assertEquals(LocalDateTime.of(2024, 6, 15, 9, 30), fetched.getCreatedAt());
    }

    // ─── Test 12: Crawl Multi-Version ────────────────────────────────

    @Test
    @Order(12)
    void crawlMultiVersionPersistAndGet() throws Exception {
        Crawl crawl = new Crawl("http://async-test.com");
        crawl.addF1(1000L, 1.1);
        crawl.addF1(2000L, 2.2);
        crawl.addF1(3000L, 3.3);

        crawlDAO.persist(crawl).get(10, TimeUnit.SECONDS);

        Crawl fetched = crawlDAO.get("http://async-test.com", 10).get(10, TimeUnit.SECONDS);
        assertNotNull(fetched);
        assertEquals("http://async-test.com", fetched.getKey());
        assertNotNull(fetched.getF1());
        assertEquals(3, fetched.getF1().size());
        assertEquals(1.1, fetched.getF1().get(1000L));
        assertEquals(3.3, fetched.getF1().get(3000L));
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    private static void waitForZookeeper(String host, int port, int maxRetries) throws Exception {
        for (int i = 0; i < maxRetries; i++) {
            try (Socket socket = new Socket(host, port)) {
                socket.getOutputStream().write("ruok".getBytes());
                socket.getOutputStream().flush();
                byte[] buf = new byte[4];
                int read = socket.getInputStream().read(buf);
                if (read == 4 && new String(buf).equals("imok")) {
                    System.out.println("ZooKeeper ready after " + (i + 1) + " attempts");
                    return;
                }
            } catch (Exception e) {
                // not ready yet
            }
            Thread.sleep(1000);
        }
        throw new RuntimeException("ZooKeeper did not become ready at " + host + ":" + port);
    }
}
