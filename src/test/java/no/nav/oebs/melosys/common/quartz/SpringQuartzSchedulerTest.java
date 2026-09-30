package no.nav.oebs.melosys.common.quartz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    void scheduler_overwritesExistingJobsSoTriggersInErrorStateAreReplaced() {
        SchedulerFactoryBean factory = scheduler.scheduler(
                mock(Trigger.class), mock(Trigger.class), mock(JobDetail.class), mock(DataSource.class));

        assertEquals(true, ReflectionTestUtils.getField(factory, "overwriteExistingJobs"));
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

        SimpleTriggerFactoryBean factory = scheduler.fakturaStatusTrigger(jobDetail, 5);
        factory.afterPropertiesSet();
        SimpleTrigger trigger = factory.getObject();

        assertNotNull(trigger);
        assertEquals(300_000L, trigger.getRepeatInterval());
        assertEquals(SimpleTrigger.REPEAT_INDEFINITELY, trigger.getRepeatCount());
        assertEquals("Qrtz_Trigger_LevFakuraStatus", trigger.getKey().getName());
    }

    @Test
    void fakturaStatusTrigger_dailyIntervalForProd() {
        JobDetail jobDetail = mock(JobDetail.class);
        when(jobDetail.getKey()).thenReturn(JobKey.jobKey("test"));

        SimpleTriggerFactoryBean factory = scheduler.fakturaStatusTrigger(jobDetail, 1440);
        factory.afterPropertiesSet();

        assertEquals(86_400_000L, factory.getObject().getRepeatInterval());
    }

    @Test
    void delayUntilNextStart_beforeStartTime_startsTodayAt0830() {
        Clock clock = clockAt("2026-09-30T07:00:00");

        assertEquals(Duration.ofMinutes(90), SpringQuartzScheduler.delayUntilNextStart(clock, Duration.ofMinutes(5)));
    }

    @Test
    void delayUntilNextStart_exactlyAtStartTime_startsNow() {
        Clock clock = clockAt("2026-09-30T08:30:00");

        assertEquals(Duration.ZERO, SpringQuartzScheduler.delayUntilNextStart(clock, Duration.ofMinutes(5)));
    }

    @Test
    void delayUntilNextStart_afterStartTimeWithShortInterval_startsAtNextInterval() {
        Clock clock = clockAt("2026-09-30T11:52:00");

        assertEquals(Duration.ofMinutes(3), SpringQuartzScheduler.delayUntilNextStart(clock, Duration.ofMinutes(5)));
    }

    @Test
    void delayUntilNextStart_afterStartTimeWithDailyInterval_startsTomorrowAt0830() {
        Clock clock = clockAt("2026-09-30T11:52:00");

        assertEquals(Duration.ofHours(20).plusMinutes(38),
                SpringQuartzScheduler.delayUntilNextStart(clock, Duration.ofMinutes(1440)));
    }

    @Test
    void delayUntilNextStart_withNonPositiveInterval_throws() {
        Clock clock = clockAt("2026-09-30T11:52:00");

        assertThrows(IllegalArgumentException.class,
                () -> SpringQuartzScheduler.delayUntilNextStart(clock, Duration.ZERO));
    }

    private static Clock clockAt(String localDateTime) {
        ZoneId zone = ZoneId.of("Europe/Oslo");
        return Clock.fixed(LocalDateTime.parse(localDateTime).atZone(zone).toInstant(), zone);
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
