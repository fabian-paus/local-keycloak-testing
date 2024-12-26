package fabianpaus.keycloak.testing.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fabianpaus.keycloak.testing.local.KcCommand.makeKcCommand;

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

    public void waitForStartup() {
        try {
            URL localUrl = new URI("http://localhost:8080").toURL();
            waitForHttpConnection(localUrl);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void waitForHttpConnection(URL url) {
        for (int i = 0; i < 30; ++i) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(2000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("LocalKeycloak: Connected after " + i + " attempts");
                    return;
                }
            } catch (IOException e) {
                // Errors are expected here since Keycloak is just starting
            }

            // Wait a little bit before trying again
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Ignored
            }
        }
        throw new RuntimeException("Could not connect to local Keycloak instance");
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
}
