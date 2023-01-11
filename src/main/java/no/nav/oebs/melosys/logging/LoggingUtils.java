package no.nav.oebs.melosys.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LoggingUtils {

    private LoggingUtils() {

    }

    /**
     * Formaterer exception-objektet til stringformat der stacktracen er formatert med linjeskift for hvert kall i tracen.
     *
     * @param exception
     *            exception-objektet.
     * @return Formatert exception; <code>null</code> dersom exception-parameteren er null.
     */
    public static String formatExceptionAsString(Throwable exception) {
        if (exception == null) {
            return null;
        }

        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));

        return stringWriter.toString();
    }
}
