package no.nav.oebs.melosys.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.melosys.db.entity.FakturaStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatusFakturaProducerServiceImpl implements StatusFakturaProducerService {

    @Autowired
    private KafkaTemplate<String, FakturaStatus> kafkaTemplate;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Override
    public void send(FakturaStatus status) {
        log.info("Melding sendt til kafka topic: {} \n Melding: {}", topic, status);
        kafkaTemplate.send(topic ,status);
    }

}
