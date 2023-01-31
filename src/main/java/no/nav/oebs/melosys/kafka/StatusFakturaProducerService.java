package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.entity.FakturaStatus;

public interface StatusFakturaProducerService {
    void send(FakturaStatus status);
}
