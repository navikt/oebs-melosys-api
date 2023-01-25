package no.nav.oebs.melosys;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.config.Props;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot applikasjonsklasse.
 */
@Slf4j
@SpringBootApplication
public class 	Application {

	public static void main(String[] args) {
		Props.setProps();
		log.info("Spring boot starting...");
		SpringApplication.run(Application.class, args);
	}
}