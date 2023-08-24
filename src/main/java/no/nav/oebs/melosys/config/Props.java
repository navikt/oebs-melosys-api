package no.nav.oebs.melosys.config;

import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class Props {

    public static String configEnv(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            return IOUtils.toString(fis, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Filen '{}' enten finnes ikke eller er tom" , fileName);
        }
        return null;
    }

//    public static String appsUserName      = configEnv("/secrets/oebs-p/apps-user/apps-username");
//    public static String appsUserPass      = configEnv("/secrets/oebs-p/apps-user/apps-password");
//    public static String jdbcUrl           = configEnv("/secrets/oebs-p/jdbc-url/url");
//    public static String appName           = configEnv("/secrets/oebs-p/app-name/app-name");
    public static String appsUserName      = configEnv("/secrets/oebsu1/apps-user/apps-username");
    public static String appsUserPass      = configEnv("/secrets/oebsu1/apps-user/apps-password");
    public static String jdbcUrl           = configEnv("/secrets/oebsu1/jdbc-url/url");
    public static String appName           = configEnv("/secrets/oebsu1/app-name/app-name");

    public static void setProps() {
        System.setProperty("spring.datasource.username", appsUserName);
        System.setProperty("spring.datasource.password", appsUserPass);
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.application.name", appName);
    }

}