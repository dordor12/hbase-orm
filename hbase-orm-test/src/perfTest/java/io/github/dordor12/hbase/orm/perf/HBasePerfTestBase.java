package io.github.dordor12.hbase.orm.perf;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.github.dordor12.hbase.orm.codec.BestSuitCodec;
import io.github.dordor12.hbase.orm.dao.AsyncHBaseDAO;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.net.Socket;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Base class for performance tests. Starts an HBase Docker container and creates
 * tables for Citizen, Employee, and Crawl entities.
 */
public abstract class HBasePerfTestBase {

    protected static final int ZK_PORT = 2181;
    protected static final int MASTER_PORT = 16000;
    protected static final int RS_PORT = 16020;
    protected static final int MASTER_UI_PORT = 16010;

    protected static GenericContainer<?> hbaseContainer;
    protected static Connection syncConnection;
    protected static AsyncConnection asyncConnection;

    protected static BestSuitCodec codec;
    protected static HBaseDAO<String, Citizen> citizenDAO;
    protected static HBaseDAO<Long, Employee> employeeDAO;
    protected static HBaseDAO<String, Crawl> crawlDAO;
    protected static AsyncHBaseDAO<String, Citizen> asyncCitizenDAO;
    protected static AsyncHBaseDAO<Long, Employee> asyncEmployeeDAO;
    protected static AsyncHBaseDAO<String, Crawl> asyncCrawlDAO;

    @BeforeAll
    @SuppressWarnings("resource")
    static void setUpHBase() throws Exception {
        String imageName = System.getenv("HBASE_TEST_IMAGE");
        if (imageName == null || imageName.isBlank()) {
            imageName = System.getProperty("hbase.test.image", "");
        }

        if (imageName.isBlank()) {
            Path dockerDir = Path.of(System.getProperty("project.dir"), "docker");
            hbaseContainer = new GenericContainer<>(
                    new ImageFromDockerfile()
                            .withDockerfile(dockerDir.resolve("Dockerfile")));
        } else {
            hbaseContainer = new GenericContainer<>(DockerImageName.parse(imageName));
        }

        hbaseContainer.withCreateContainerCmdModifier(cmd -> {
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

        syncConnection = ConnectionFactory.createConnection(conf);
        asyncConnection = ConnectionFactory.createAsyncConnection(conf).get(30, TimeUnit.SECONDS);

        createNamespacesAndTables();

        codec = new BestSuitCodec();
        citizenDAO = new HBaseDAO<>(syncConnection, new CitizenHBMapper(codec));
        employeeDAO = new HBaseDAO<>(syncConnection, new EmployeeHBMapper(codec));
        crawlDAO = new HBaseDAO<>(syncConnection, new CrawlHBMapper(codec));
        asyncCitizenDAO = new AsyncHBaseDAO<>(asyncConnection, new CitizenHBMapper(codec));
        asyncEmployeeDAO = new AsyncHBaseDAO<>(asyncConnection, new EmployeeHBMapper(codec));
        asyncCrawlDAO = new AsyncHBaseDAO<>(asyncConnection, new CrawlHBMapper(codec));
    }

    @AfterAll
    static void tearDownHBase() throws Exception {
        if (asyncConnection != null) asyncConnection.close();
        if (syncConnection != null) syncConnection.close();
        if (hbaseContainer != null) hbaseContainer.stop();
    }

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
