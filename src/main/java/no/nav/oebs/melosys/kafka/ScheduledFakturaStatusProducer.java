package no.nav.oebs.melosys.kafka;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ScheduledFakturaStatusProducer  implements Job{

    @Autowired
    StatusFakturaProducerService statusFakturaProducerService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        statusFakturaProducerService.hentOgSplitFakturaStatus();
    }

    // hent alle faktura status siden sist kjøring

    // send alle faktura status til topic

    // lagre i kallLogg
}
