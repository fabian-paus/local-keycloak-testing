package fabianpaus.keycloak.testing.local;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the functionality of the bin/kc.bat/.sh command line utility.
 * <p>
 * Usually, you start Keycloak via the bin/kc command line utility.
 * This program builds the command line arguments for the Java virtual machine to start a Keycloak instance.
 * This class builds these command line arguments in Java code to start the Keycloak without the need
 * for this command line utility.
 * </p>
 */
public class KcCommand {
    /**
     * Builds a command to start a Keycloak instance.
     *
     * @param home  Home path of the Keycloak distribution, see KeycloakDistribution.
     * @param build Whether the Keycloak should be built or started.
     * @return Command list, including the Java program and arguments.
     */
    public static List<String> makeKcCommand(Path home, boolean build) {

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
