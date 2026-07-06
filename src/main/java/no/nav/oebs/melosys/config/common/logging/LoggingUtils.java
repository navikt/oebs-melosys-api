package no.nav.oebs.melosys.config.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

/**
 * Hjelpemetoder relatert til logging.
 */
public class LoggingUtils {

	private static final Pattern FNR_PATTERN = Pattern.compile("(\\D+|^)(\\d{2})\\d{7}(\\d{2})(\\D+|$)");


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
		var stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));

		return stringWriter.toString();
	}

	/**
	 * Maskerer dersom teksten ser ut til å inneholde et fødselsnummer slik at kun de to første og de to siste tallene vises i
	 * klartekst.
	 *
	 * @param text
	 *            en string der fødseslnummeret skal maskeres.
	 * @return Inputstringen med maskert fødselsnummer; "(null)" dersom input er null.
	 *
	 * @implNote Regexen fungerer ikke for flere fødselsnummere i samme tekst. I et slikt tilfelle vil kun første fnr bli
	 *           maskert.
	 */
	public static String maskIfFnr(String text) {
		if (text == null) {
			return "(null)";
		}
		return FNR_PATTERN.matcher(text).replaceAll("$1$2" + "*******" + "$3$4");
	}
}
