package no.nav.oebs.melosys.exception;

/**
 * Exception som kastes ved avbrudd under sending til Kafka.
 */
public class KafkaProducerInterruptedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public KafkaProducerInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}
}
