package fabianpaus.keycloak.testing.local;

import java.nio.file.Path;

/**
 * Starts a local Keycloak instance for integration tests.
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
public class KeycloakInstance {
    private final Path home;

    public KeycloakInstance(String version) {
        this.home = Downloader.download(version, Path.of("downloads"));
    }

    public Path getHome() {
        return home;
    }
}
