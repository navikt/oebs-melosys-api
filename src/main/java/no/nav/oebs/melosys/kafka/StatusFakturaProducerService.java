package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.entity.FakturaStatus;

import java.util.concurrent.ExecutionException;

public interface StatusFakturaProducerService {
    void send(FakturaStatus status) throws ExecutionException, InterruptedException;
}
