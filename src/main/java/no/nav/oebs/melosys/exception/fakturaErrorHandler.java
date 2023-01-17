package no.nav.oebs.melosys.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class fakturaErrorHandler implements ConsumerAwareListenerErrorHandler {


    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException e, Consumer<?, ?> consumer) {
        log.error("Feil under lytting: {}", message, e.getCause());
        Exception cause = (Exception) e.getCause();
        if(cause instanceof Exception) {
            // handle some exception
        }
        return null;
    }
}
