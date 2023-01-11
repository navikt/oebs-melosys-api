package no.nav.oebs.melosys.config;

import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
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

    public static String appsUserName      = configEnv("/secrets/oebs-p/apps-user/apps-username");
    public static String appsUserPass      = configEnv("/secrets/oebs-p/apps-user/apps-password");
    public static String jdbcUrl           = configEnv("/secrets/oebs-p/jdbc-url/url");
    public static String appName           = configEnv("/secrets/oebs-p/app-name/app-name");
    public static String api_loadaccounts  = configEnv("/secrets/oebs-p/vieri-apim/api-loadaccounts");
    public static String api_loadsuppliers = configEnv("/secrets/oebs-p/vieri-apim/api-loadsuppliers");
    public static String api_loaddimensions = configEnv("/secrets/oebs-p/vieri-apim/api-loaddimensions");
    public static String ocp_apim          = configEnv("/secrets/oebs-p/vieri-apim/ocp-apim");


   /* public static String appsUserName    = configEnv("/home/apploebsq2/abou/secrets/oebsq2/apps-user/apps-username");
    public static String appsUserPass      = configEnv("/home/apploebsq2/abou/secrets/oebsq2/apps-user/apps-password");
    public static String jdbcUrl           = configEnv("/home/apploebsq2/abou/secrets/oebsq2/jdbc-url/url");
    public static String appName           = configEnv("/home/apploebsq2/abou/secrets/oebsq2/app-name/app-name");
    public static String api_loadaccounts  = configEnv("/home/apploebsq2/abou/secrets/oebsq2/vieri-apim/api-loadaccounts");
    public static String api_loadsuppliers = configEnv("/home/apploebsq2/abou/secrets/oebsq2/vieri-apim/api-loadsuppliers");
    public static String ocp_apim          = configEnv("/home/apploebsq2/abou/secrets/oebsq2/vieri-apim/ocp-apim");
*/
    public static void setProps() {
        System.setProperty("spring.datasource.username", appsUserName);
        System.setProperty("spring.datasource.password", appsUserPass);
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.application.name", appName);
        System.setProperty("vieri.api.loadaccounts", api_loadaccounts);
        System.setProperty("vieri.api.loadsuppliers", api_loadsuppliers);
        System.setProperty("vieri.api.loaddimensions", api_loaddimensions);
        System.setProperty("vieri.ocp.management", ocp_apim);
    }

}