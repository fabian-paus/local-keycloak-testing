package fabianpaus.keycloak.testing.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A running Keycloak instance for integration testing.
 * <p>
 * After downloading a Keycloak distribution via the KeycloakDistribution class,
 * you can start a local Keycloak instance. The server will open an HTTP port that
 * can be used to access the admin API as well as the web frontend.
 * </p>
 */
public class KeycloakInstance {
    private Process process;
    private final Thread outputReader;
    private final List<String> log = Collections.synchronizedList(new ArrayList<>());

    public KeycloakInstance(Process process) {
        // Make sure to always close this process even if we unexpectedly close
        Thread shutdownHook = new Thread(this::close);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        this.process = process;
        this.outputReader = new Thread(() -> {
            InputStream stream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    log.add(line);
                }
            } catch (IOException e) {
                System.err.println("LocalKeycloak: Error while reading output");
                System.err.println(e.getClass().getTypeName() + ": " + e.getMessage());
            }
        });
        this.outputReader.start();
    }

    public static KeycloakInstance start(Path home) {
        build(home);

        List<String> command = makeKcCommand(home, false);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        builder.redirectErrorStream(true);

        try {
            Process process = builder.start();
            return new KeycloakInstance(process);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public synchronized void close() {
        if (this.process == null) return;

        System.out.println("LocalKeycloak: Exiting process");
        try {
            this.process.destroy();
            int exitCode = this.process.waitFor();
            this.outputReader.join();
            System.out.println("LocalKeycloak: Process has exited with code " + exitCode);
            this.process = null;
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void build(Path home) {
        List<String> command = makeKcCommand(home, true);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        builder.inheritIO();

        try {
            Process process = builder.start();

            int result = process.waitFor();
            if (result != 0) {
                System.err.println("LocalKeycloak: Failed to build with exit code " + result);
                throw new RuntimeException("Failed to build: " + result);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> makeKcCommand(Path home, boolean build) {

        List<String> commands = new ArrayList<>();
        commands.add("java");

        if (build) {
            commands.add("-Dkc.config.build-and-exit=true");
        } else {
            commands.add("-Dkc.config.built=true");
        }

        // Taken from kc.bat
        commands.add("-Djava.util.concurrent.ForkJoinPool.common.threadFactory=io.quarkus.bootstrap.forkjoin.QuarkusForkJoinWorkerThreadFactory");
        commands.add("-Dprogram.name=kc.bat");

        // Configure metaspace size
        commands.add("-XX:MetaspaceSize=96M");
        commands.add("-XX:MaxMetaspaceSize=256m");
        commands.add("-XX:+ExitOnOutOfMemoryError");

        // Default encoding: UTF-8
        commands.add("-Dfile.encoding=UTF-8");
        commands.add("-Dsun.stdout.encoding=UTF-8");
        commands.add("-Dsun.err.encoding=UTF-8");
        commands.add("-Dstdout.encoding=UTF-8");
        commands.add("-Dstderr.encoding=UTF-8");

        commands.add("-Djava.security.egd=file:/dev/urandom");
        commands.add("-XX:+UseG1GC");
        commands.add("-XX:FlightRecorderOptions=stackdepth=512");
        commands.add("-Xms64m");
        commands.add("-Xmx512m");

        commands.add("--add-opens=java.base/java.util=ALL-UNNAMED");
        commands.add("--add-opens=java.base/java.util.concurrent=ALL-UNNAMED");
        commands.add("--add-opens=java.base/java.security=ALL-UNNAMED");

        commands.add("-Duser.language=en");
        commands.add("-Duser.country=US");

        commands.add("-Dkc.home.dir=\"" + home + "\"");
        commands.add("-Djboss.server.config.dir=\"" + home.resolve("conf") + "\"");
        commands.add("-Dkeycloak.theme.dir=\"" + home.resolve("themes") + "\"");

        commands.add("-Djava.util.logging.manager=org.jboss.logmanager.LogManager");
        commands.add("-Dquarkus-log-max-startup-records=10000");
        commands.add("-Dpicocli.disable.closures=true");

        // Class path
        commands.add("-cp");
        commands.add(home.resolve("lib/quarkus-run.jar").toString());

        // Java entry point, i.e. the main function
        commands.add("io.quarkus.bootstrap.runner.QuarkusEntryPoint");

        // Keycloak arguments
        commands.add("--profile=dev");

        commands.add("start-dev");

        return commands;
    }
}
