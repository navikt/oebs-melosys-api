package no.nav.oebs.melosys.kafka;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

@Slf4j
public class ScheduledFakturaStatusProducer  implements Job{

    private final StatusFakturaProducerService statusFakturaProducerService;

    public ScheduledFakturaStatusProducer(StatusFakturaProducerService statusFakturaProducerService) {
        this.statusFakturaProducerService = statusFakturaProducerService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        statusFakturaProducerService.hentOgSplitFakturaStatus();
    }

}