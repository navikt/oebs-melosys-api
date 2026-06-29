package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.entity.FakturaStatus;
import no.nav.oebs.melosys.db.entity.FakturaStatusFeilImport;
import no.nav.oebs.melosys.db.repository.PlsqlProcedureResult;

import java.util.concurrent.ExecutionException;

public interface StatusFakturaProducerService {
    void sendFakturaStatus(FakturaStatus status, PlsqlProcedureResult result, String procedureName) throws ExecutionException, InterruptedException;

    void sendFakturaStatusVedFeil(FakturaStatusFeilImport fakturaStatus);
}
