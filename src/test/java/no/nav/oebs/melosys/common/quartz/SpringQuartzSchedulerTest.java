package no.nav.oebs.melosys.common.quartz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import no.nav.oebs.melosys.kafka.ScheduledFakturaStatusProducer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringQuartzSchedulerTest {

    @Mock
    private ApplicationContext applicationContext;

    private SpringQuartzScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new SpringQuartzScheduler(applicationContext);
    }

    @Test
    void init_doesNotThrow() {
        assertDoesNotThrow(() -> scheduler.init());
    }

    @Test
    void springBeanJobFactory_returnsNonNull() {
        SpringBeanJobFactory factory = scheduler.springBeanJobFactory();

        assertNotNull(factory);
    }

    @Test
    void levFakturaStatus_returnsJobDetailWithCorrectConfiguration() {
        JobDetailFactoryBean factory = scheduler.levFakturaStatus();
        factory.afterPropertiesSet();
        JobDetail jobDetail = factory.getObject();

        assertNotNull(jobDetail);
        assertEquals("Qrtz_LevFakturaStatus_kafka", jobDetail.getKey().getName());
        assertEquals(ScheduledFakturaStatusProducer.class, jobDetail.getJobClass());
        assertTrue(jobDetail.isDurable());
    }

    @Test
    void fakturaStatusTrigger_returnsCorrectRepeatInterval() {
        JobDetail jobDetail = mock(JobDetail.class);
        when(jobDetail.getKey()).thenReturn(JobKey.jobKey("test"));

        SimpleTriggerFactoryBean factory = scheduler.fakturaStatusTrigger(jobDetail);
        factory.afterPropertiesSet();
        SimpleTrigger trigger = factory.getObject();

        assertNotNull(trigger);
        assertEquals(300_000L, trigger.getRepeatInterval());
        assertEquals(SimpleTrigger.REPEAT_INDEFINITELY, trigger.getRepeatCount());
        assertEquals("Qrtz_Trigger_LevFakuraStatus", trigger.getKey().getName());
    }

    @Test
    void fakturaStatusTriggerOnStartup_returnsCorrectConfiguration() {
        JobDetail jobDetail = mock(JobDetail.class);
        when(jobDetail.getKey()).thenReturn(JobKey.jobKey("test"));

        SimpleTriggerFactoryBean factory = scheduler.fakturaStatusTriggerOnStartup(jobDetail);
        SimpleTrigger trigger = factory.getObject();

        assertNotNull(trigger);
        assertEquals(0, trigger.getRepeatCount());
        assertEquals("Qrtz_Trigger_LevFakuraStatusOnStartUp", trigger.getKey().getName());
    }
}
