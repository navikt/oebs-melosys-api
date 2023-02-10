package no.nav.oebs.melosys.kafka;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;

@Slf4j
public class ScheduledFakturaStatusProducer  implements Job{

    @Autowired
    StatusFakturaProducerService statusFakturaProducerService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Jeg later som jeg henter fakura via prosedyre 🎉🎉🎉");
        //statusFakturaProducerService.hentFakturaStatusOgSend();
    }

    // hent alle faktura status siden sist kjøring

    // send alle faktura status til topic

    // lagre i kallLogg
}
