package fabianpaus.keycloak.testing.local;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeycloakInstanceTest {

    @Test
    public void download() {
        KeycloakInstance instance = new KeycloakInstance("26.0.7");
        Assertions.assertTrue(instance.getHome().toFile().exists());
    }
}
