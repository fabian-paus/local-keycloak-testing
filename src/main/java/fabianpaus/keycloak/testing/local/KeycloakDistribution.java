package fabianpaus.keycloak.testing.local;

import java.nio.file.Path;

/**
 * Downloads and configures a local Keycloak distribution for integration tests.
 * <p>
 * You only need to specify the Keycloak version that you want to run, e.g. 26.0.7.
 * After downloading and extracting the specified version, the Keycloak can be setup.
 * This includes loading of customized extensions and themes as JAR files.
 * Furthermore, exported configuration can be imported to set up a defined configuration.
 * </p>
 * <p>
 * After the Keycloak has started, you can use a Keycloak admin client to adapt the configuration,
 * create users and set up clients for authentication.
 * </p>
 */
public class KeycloakDistribution {
    private final Path home;

    public KeycloakDistribution(Path home) {
        this.home = home;
    }

    public static KeycloakDistribution download(String version, String downloadPath) {
        Path home = Downloader.download(version, Path.of(downloadPath));
        System.out.println("LocalKeycloak: Downloaded to " + home.toAbsolutePath());
        return new KeycloakDistribution(home.toAbsolutePath());
    }

    public Path getHome() {
        return home;
    }

    public KeycloakInstance start() {
        return KeycloakInstance.start(this.home);
    }
}
