package fabianpaus.keycloak.testing.local;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
    private final Process process;

    public KeycloakInstance(Process process) {
        this.process = process;
    }

    public static KeycloakInstance start(Path home) {
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("--version");

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);

        try {
            Process process = builder.start();
            return new KeycloakInstance(process);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void close() {
        if (this.process == null) return;

        try {
            this.process.destroy();
            this.process.waitFor();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
