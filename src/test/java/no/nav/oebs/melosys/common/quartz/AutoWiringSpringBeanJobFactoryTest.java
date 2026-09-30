package no.nav.oebs.melosys.common.quartz;

import no.nav.oebs.melosys.kafka.ScheduledFakturaStatusProducer;
import no.nav.oebs.melosys.kafka.StatusFakturaProducerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutoWiringSpringBeanJobFactoryTest {

    private GenericApplicationContext applicationContext;
    private StatusFakturaProducerService statusFakturaProducerService;

    @BeforeEach
    void setUp() {
        statusFakturaProducerService = mock(StatusFakturaProducerService.class);
        applicationContext = new GenericApplicationContext();
        applicationContext.registerBean(StatusFakturaProducerService.class, () -> statusFakturaProducerService);
        applicationContext.refresh();
    }

    @AfterEach
    void tearDown() {
        applicationContext.close();
    }

    @Test
    void createJobInstance_injectsConstructorDependenciesIntoJob() throws Exception {
        AutoWiringSpringBeanJobFactory factory = new AutoWiringSpringBeanJobFactory();
        factory.setApplicationContext(applicationContext);

        JobDetail jobDetail = JobBuilder.newJob(ScheduledFakturaStatusProducer.class).build();
        OperableTrigger trigger = mock(OperableTrigger.class);
        TriggerFiredBundle bundle = mock(TriggerFiredBundle.class);
        when(bundle.getJobDetail()).thenReturn(jobDetail);
        when(bundle.getTrigger()).thenReturn(trigger);

        Object job = factory.createJobInstance(bundle);

        ScheduledFakturaStatusProducer producer = assertInstanceOf(ScheduledFakturaStatusProducer.class, job);
        producer.execute(null);
        verify(statusFakturaProducerService).hentOgSplitFakturaStatus();
    }
}
