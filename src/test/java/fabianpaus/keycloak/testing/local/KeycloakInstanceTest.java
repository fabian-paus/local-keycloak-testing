package fabianpaus.keycloak.testing.local;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeycloakInstanceTest {
    private static final String KEYCLOAK_VERSION = "26.0.7";

    @Test
    public void download() {
        KeycloakDistribution distribution = KeycloakDistribution.download(KEYCLOAK_VERSION, "downloads");

        Assertions.assertTrue(distribution.getHome().toFile().exists());
    }

    @Test
    public void start() {
        KeycloakDistribution distribution = KeycloakDistribution.download(KEYCLOAK_VERSION, "downloads");

        KeycloakInstance instance = distribution.start();
    }
}
