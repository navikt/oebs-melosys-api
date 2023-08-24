package no.nav.oebs.melosys.kafka;

import no.nav.oebs.melosys.db.entity.FakturaStatus;
import no.nav.oebs.melosys.db.entity.FakturaStatusFeilImport;

import java.util.concurrent.ExecutionException;

public interface StatusFakturaProducerService {
    void sendFakturaStatus(FakturaStatus status) throws ExecutionException, InterruptedException;

    void hentOgSplitFakturaStatus();

    void sendFakturaStatusVedFeil(FakturaStatusFeilImport fakturaStatus);
}
