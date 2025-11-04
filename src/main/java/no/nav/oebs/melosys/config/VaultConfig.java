package no.nav.oebs.melosys.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:/var/run/secrets/nais.io/vault/" + VaultConfig.PROPERTY_FILE)
public class VaultConfig {

    static final String PROPERTY_FILE = "secrets.properties";
}
