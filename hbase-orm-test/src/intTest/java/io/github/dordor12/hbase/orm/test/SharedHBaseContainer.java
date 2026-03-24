package io.github.dordor12.hbase.orm.test;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.net.Socket;
import java.time.Duration;

/**
 * Singleton HBase container shared across all integration test classes.
 * Starts once and stays alive for the entire test suite run.
 */
final class SharedHBaseContainer {

    static final int ZK_PORT = 2181;
    static final int MASTER_PORT = 16000;
    static final int RS_PORT = 16020;
    static final int MASTER_UI_PORT = 16010;

    private static GenericContainer<?> container;

    private SharedHBaseContainer() {}

    static synchronized GenericContainer<?> get() throws Exception {
        if (container != null && container.isRunning()) {
            return container;
        }
        container = createContainer();
        container.start();
        waitForZookeeper("localhost", ZK_PORT, 60);
        return container;
    }

    private static GenericContainer<?> createContainer() {
        String imageName = System.getenv("HBASE_TEST_IMAGE");
        if (imageName == null || imageName.isBlank()) {
            imageName = System.getProperty("hbase.test.image", "");
        }

        GenericContainer<?> c;
        if (imageName.isBlank()) {
            java.nio.file.Path dockerDir = java.nio.file.Path.of(
                    System.getProperty("project.dir"), "docker");
            c = new GenericContainer<>(
                    new ImageFromDockerfile()
                            .withDockerfile(dockerDir.resolve("Dockerfile")));
        } else {
            c = new GenericContainer<>(DockerImageName.parse(imageName));
        }

        return c.withCreateContainerCmdModifier(cmd -> {
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
