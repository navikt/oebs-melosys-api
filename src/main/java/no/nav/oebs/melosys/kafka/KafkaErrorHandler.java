package no.nav.oebs.melosys.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "kafkaErrorHandler")
public class KafkaErrorHandler implements ConsumerAwareListenerErrorHandler{


    public Object handleError(Message<?> message, ListenerExecutionFailedException e, Consumer<?, ?> consumer) {
        log.warn("Exception thrown during kafka consuming with error message: {} and cause: {}",
                e.getMessage(), e.getCause() != null ? e.getCause().toString() : "unknown cause");

        return null;
    }
}
