package io.github.dordor12.hbase.orm.test;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.dao.HBaseDAO;
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
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.math.BigDecimal;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that verify the generated ORM mappers against a real HBase instance
 * running in a Docker container via Testcontainers.
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HBaseIntegrationIT {

    private static final int ZK_PORT = 2181;
    private static final int MASTER_PORT = 16000;
    private static final int RS_PORT = 16020;
    private static final int MASTER_UI_PORT = 16010;

    private static GenericContainer<?> hbaseContainer;
    private static Connection connection;

    private static HBaseDAO<String, Citizen> citizenDAO;
    private static HBaseDAO<Long, Employee> employeeDAO;
    private static HBaseDAO<String, Crawl> crawlDAO;

    @BeforeAll
    @SuppressWarnings("resource")
    static void setUp() throws Exception {
        java.nio.file.Path dockerDir = java.nio.file.Path.of(System.getProperty("project.dir"), "docker");
        hbaseContainer = new GenericContainer<>(
                new ImageFromDockerfile()
                        .withDockerfile(dockerDir.resolve("Dockerfile")))
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
        connection = ConnectionFactory.createConnection(conf);

        createNamespacesAndTables();

        BestSuitCodec codec = new BestSuitCodec();
        citizenDAO = new HBaseDAO<>(connection, new CitizenHBMapper(codec));
        employeeDAO = new HBaseDAO<>(connection, new EmployeeHBMapper(codec));
        crawlDAO = new HBaseDAO<>(connection, new CrawlHBMapper(codec));
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (connection != null) connection.close();
        if (hbaseContainer != null) hbaseContainer.stop();
    }

    // ─── Table Setup ─────────────────────────────────────────────────

    private static void createNamespacesAndTables() throws Exception {
        try (Admin admin = connection.getAdmin()) {
            // Create 'govt' namespace for Citizen
            try {
                admin.createNamespace(NamespaceDescriptor.create("govt").build());
            } catch (Exception e) {
                // namespace may already exist
            }

            // Create Citizen table: govt:citizens with families "main" and "optional"
            createTableFromMapper(admin, new CitizenHBMapper(new BestSuitCodec()));

            // Create Employee table: employees with family "a"
            createTableFromMapper(admin, new EmployeeHBMapper(new BestSuitCodec()));

            // Create Crawl table: crawls with family "a" (10 versions)
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

    // ─── Citizen Tests ───────────────────────────────────────────────

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

        String rowKey = citizenDAO.persist(citizen);
        assertEquals("US#42", rowKey);

        Citizen fetched = citizenDAO.get("US#42");
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

    @Test
    @Order(2)
    void citizenBulkPersistAndGet() throws Exception {
        List<Citizen> citizens = List.of(
                new Citizen("US", 100, "Bob"),
                new Citizen("US", 101, "Carol"),
                new Citizen("GB", 200, "Dave")
        );

        List<String> keys = citizenDAO.persist(citizens);
        assertEquals(3, keys.size());
        assertEquals("US#100", keys.get(0));

        List<Citizen> fetched = citizenDAO.get(List.of("US#100", "US#101", "GB#200"));
        assertEquals(3, fetched.size());
    }

    @Test
    @Order(3)
    void citizenExists() throws Exception {
        assertTrue(citizenDAO.exists("US#42"));
        assertFalse(citizenDAO.exists("XX#999"));
    }

    @Test
    @Order(4)
    void citizenDelete() throws Exception {
        Citizen temp = new Citizen("DE", 1, "Temp");
        citizenDAO.persist(temp);
        assertTrue(citizenDAO.exists("DE#1"));

        citizenDAO.delete("DE#1");
        assertFalse(citizenDAO.exists("DE#1"));
    }

    @Test
    @Order(5)
    void citizenMultiVersionField() throws Exception {
        Citizen citizen = new Citizen("JP", 1, "Yuki");
        NavigableMap<Long, Integer> phones = new TreeMap<>();
        phones.put(1000L, 111);
        phones.put(2000L, 222);
        phones.put(3000L, 333);
        citizen.setPhoneNumber(phones);

        citizenDAO.persist(citizen);

        // Fetch with multiple versions
        Citizen fetched = citizenDAO.get("JP#1", 10);
        assertNotNull(fetched);
        assertEquals("Yuki", fetched.getName());
        assertNotNull(fetched.getPhoneNumber());
        // Multi-version columns: all 3 timestamps should be stored
        assertEquals(3, fetched.getPhoneNumber().size());
        assertEquals(Integer.valueOf(111), fetched.getPhoneNumber().get(1000L));
        assertEquals(Integer.valueOf(222), fetched.getPhoneNumber().get(2000L));
        assertEquals(Integer.valueOf(333), fetched.getPhoneNumber().get(3000L));
    }

    @Test
    @Order(6)
    void citizenScan() throws Exception {
        // Scan all US citizens (US#42, US#100, US#101 from earlier tests)
        List<Citizen> usCitizens = citizenDAO.getByPrefix(Bytes.toBytes("US#"));
        assertTrue(usCitizens.size() >= 3, "Expected at least 3 US citizens, got " + usCitizens.size());
        for (Citizen c : usCitizens) {
            assertEquals("US", c.getCountryCode());
        }
    }

    // ─── Employee Tests ──────────────────────────────────────────────

    @Test
    @Order(10)
    void employeePersistAndGet() throws Exception {
        Employee emp = new Employee(1L, "Engineer");
        emp.setReporteeCount((short) 5);
        emp.setCreatedAt(LocalDateTime.of(2024, 6, 15, 9, 30));

        Long rowKey = employeeDAO.persist(emp);
        assertEquals(Long.valueOf(1), rowKey);

        Employee fetched = employeeDAO.get(1L);
        assertNotNull(fetched);
        assertEquals(Long.valueOf(1), fetched.getEmpid());
        assertEquals("Engineer", fetched.getEmpName());
        assertEquals(Short.valueOf((short) 5), fetched.getReporteeCount());
        // Inherited field from @MappedSuperclass
        assertEquals(LocalDateTime.of(2024, 6, 15, 9, 30), fetched.getCreatedAt());
    }

    @Test
    @Order(11)
    void employeeInheritedFieldNullRoundtrip() throws Exception {
        // Persist without setting the inherited createdAt field
        Employee emp = new Employee(2L, "Designer");
        emp.setReporteeCount((short) 3);

        employeeDAO.persist(emp);

        Employee fetched = employeeDAO.get(2L);
        assertNotNull(fetched);
        assertEquals("Designer", fetched.getEmpName());
        assertEquals(Short.valueOf((short) 3), fetched.getReporteeCount());
        assertNull(fetched.getCreatedAt(), "Inherited field should be null when not set");
    }

    @Test
    @Order(12)
    void employeeUpdateInheritedField() throws Exception {
        // Re-persist employee 1 with a different createdAt
        Employee emp = new Employee(1L, "Engineer");
        emp.setReporteeCount((short) 5);
        emp.setCreatedAt(LocalDateTime.of(2025, 1, 1, 0, 0));

        employeeDAO.persist(emp);

        Employee fetched = employeeDAO.get(1L);
        assertNotNull(fetched);
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), fetched.getCreatedAt(),
                "Inherited field should reflect the updated value");
    }

    @Test
    @Order(13)
    void employeeBulkWithInheritedFields() throws Exception {
        Employee e1 = new Employee(10L, "Alice");
        e1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 8, 0));

        Employee e2 = new Employee(11L, "Bob");
        e2.setCreatedAt(LocalDateTime.of(2024, 2, 1, 9, 0));

        Employee e3 = new Employee(12L, "Carol");
        // e3 has no createdAt

        employeeDAO.persist(List.of(e1, e2, e3));

        List<Employee> fetched = employeeDAO.get(List.of(10L, 11L, 12L));
        assertEquals(3, fetched.size());

        // Results come back sorted by row key (Long bytes), verify each
        Map<Long, Employee> byId = new HashMap<>();
        for (Employee e : fetched) {
            byId.put(e.getEmpid(), e);
        }

        assertEquals(LocalDateTime.of(2024, 1, 1, 8, 0), byId.get(10L).getCreatedAt());
        assertEquals(LocalDateTime.of(2024, 2, 1, 9, 0), byId.get(11L).getCreatedAt());
        assertNull(byId.get(12L).getCreatedAt());
    }

    @Test
    @Order(14)
    void employeeDeleteAndRecreatWithInheritedField() throws Exception {
        // Delete employee 10, then re-persist with different inherited field value
        employeeDAO.delete(10L);
        assertFalse(employeeDAO.exists(10L));

        Employee emp = new Employee(10L, "Alice-v2");
        emp.setCreatedAt(LocalDateTime.of(2026, 3, 23, 12, 0));
        employeeDAO.persist(emp);

        Employee fetched = employeeDAO.get(10L);
        assertNotNull(fetched);
        assertEquals("Alice-v2", fetched.getEmpName());
        assertEquals(LocalDateTime.of(2026, 3, 23, 12, 0), fetched.getCreatedAt());
    }

    @Test
    @Order(15)
    void citizenIncrementLongField() throws Exception {
        Citizen citizen = new Citizen("US", 500, "Counter");
        citizen.setF3(0L);
        citizenDAO.persist(citizen);

        // Increment f3 (Long field) by 10
        long newVal = citizenDAO.increment("US#500", "f3", 10);
        assertEquals(10, newVal);

        // Increment again
        newVal = citizenDAO.increment("US#500", "f3", 5);
        assertEquals(15, newVal);
    }

    // ─── Crawl Tests ─────────────────────────────────────────────────

    @Test
    @Order(20)
    void crawlMultiVersionPersistAndGet() throws Exception {
        Crawl crawl = new Crawl("http://example.com");
        crawl.addF1(1000L, 1.1);
        crawl.addF1(2000L, 2.2);
        crawl.addF1(3000L, 3.3);

        crawlDAO.persist(crawl);

        Crawl fetched = crawlDAO.get("http://example.com", 10);
        assertNotNull(fetched);
        assertEquals("http://example.com", fetched.getKey());
        assertNotNull(fetched.getF1());
        assertEquals(3, fetched.getF1().size());
        assertEquals(1.1, fetched.getF1().get(1000L));
        assertEquals(3.3, fetched.getF1().get(3000L));
    }

    @Test
    @Order(21)
    void crawlOverwriteVersions() throws Exception {
        // Write new versions for same key
        Crawl crawl = new Crawl("http://example.com");
        crawl.addF1(4000L, 4.4);
        crawl.addF1(5000L, 5.5);
        crawlDAO.persist(crawl);

        // Fetch all versions (max 10)
        Crawl fetched = crawlDAO.get("http://example.com", 10);
        assertNotNull(fetched.getF1());
        // Should have all 5 versions now
        assertTrue(fetched.getF1().size() >= 4, "Expected at least 4 versions");
    }

    // ─── Cross-Entity Table Metadata ─────────────────────────────────

    @Test
    @Order(30)
    void tableMetadataMatchesHBase() throws Exception {
        try (Admin admin = connection.getAdmin()) {
            // Verify Citizen table
            TableDescriptor citizenDesc = admin.getDescriptor(TableName.valueOf("govt:citizens"));
            assertNotNull(citizenDesc.getColumnFamily(Bytes.toBytes("main")));
            assertNotNull(citizenDesc.getColumnFamily(Bytes.toBytes("optional")));
            assertEquals(10, citizenDesc.getColumnFamily(Bytes.toBytes("optional")).getMaxVersions());

            // Verify Employee table
            TableDescriptor empDesc = admin.getDescriptor(TableName.valueOf("employees"));
            assertNotNull(empDesc.getColumnFamily(Bytes.toBytes("a")));

            // Verify Crawl table
            TableDescriptor crawlDesc = admin.getDescriptor(TableName.valueOf("crawls"));
            assertEquals(10, crawlDesc.getColumnFamily(Bytes.toBytes("a")).getMaxVersions());
        }
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
