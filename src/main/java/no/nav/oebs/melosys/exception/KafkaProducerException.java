package no.nav.oebs.melosys.exception;

/**
 * Exception som kastes ved feil under sending til Kafka.
 */
public class KafkaProducerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public KafkaProducerException(String message, Throwable cause) {
		super(message, cause);
	}
}
