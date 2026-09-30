package no.nav.oebs.melosys.common.quartz;

import no.nav.oebs.melosys.kafka.ScheduledFakturaStatusProducer;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.*;

import javax.sql.DataSource;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Configuration
@EnableAutoConfiguration
@ConditionalOnExpression("'${using.spring.schedulerFactory}'=='true'")
public class SpringQuartzScheduler {

    private static final LocalTime START_TIME = LocalTime.of(8, 30);
    private static final ZoneId TIME_ZONE = ZoneId.of("Europe/Oslo");

    Logger logger = LoggerFactory.getLogger(getClass());

    private final ApplicationContext applicationContext;

    public SpringQuartzScheduler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        logger.info("SpringQuartzScheduler has been initialized");
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        logger.debug("Konfigurering factory Job");

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
    @Bean
    public SchedulerFactoryBean scheduler(@Qualifier("fakturaStatusTrigger") Trigger trigger1,
                                          @Qualifier("fakturaStatusTriggerOnStartup") Trigger trigger2,
                                          JobDetail job,
                                          DataSource dataSource) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        logger.debug("Setter datasource");
        schedulerFactory.setDataSource(dataSource);

        logger.debug("Setter opp skedulering ...");
        schedulerFactory.setJobFactory(springBeanJobFactory());

        logger.debug("Setter JobDetails: {}", job);
        schedulerFactory.setJobDetails(job);

        logger.debug("Setter Trigger");
        schedulerFactory.setTriggers(trigger1, trigger2);
        schedulerFactory.setOverwriteExistingJobs(true);

        return schedulerFactory;
    }

    @Bean(name = "levFakturaStatus")
    public JobDetailFactoryBean levFakturaStatus() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(ScheduledFakturaStatusProducer.class);
        jobDetailFactory.setName("Qrtz_LevFakturaStatus_kafka");
        jobDetailFactory.setDescription("Start skedulert kafka producer for fakturastatus...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean fakturaStatusTrigger(@Qualifier("levFakturaStatus") JobDetail job,
                                                         @Value("${app.fakturastatus.interval-minutes}") long intervalMinutes) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setStartDelay(delayUntilNextStart(Clock.system(TIME_ZONE), Duration.ofMinutes(intervalMinutes)).toMillis());
        trigger.setRepeatInterval(Duration.ofMinutes(intervalMinutes).toMillis());
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Qrtz_Trigger_LevFakuraStatus");
        return trigger;
    }

    /**
     * Tid til første kjøring: kl. 08:30 i dag, eller neste intervall etter 08:30 dersom tidspunktet er passert.
     */
    static Duration delayUntilNextStart(Clock clock, Duration interval) {
        if (interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException("Interval must be positive, was " + interval);
        }
        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime start = now.toLocalDate().atTime(START_TIME).atZone(clock.getZone());
        if (start.isBefore(now)) {
            long passedIntervals = Duration.between(start, now).toMillis() / interval.toMillis() + 1;
            start = start.plus(interval.multipliedBy(passedIntervals));
        }
        return Duration.between(now, start);
    }

    @Bean
    public SimpleTriggerFactoryBean fakturaStatusTriggerOnStartup(@Qualifier("levFakturaStatus") JobDetail job){
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setStartDelay(0L);
        trigger.setRepeatCount(0);
        trigger.setName("Qrtz_Trigger_LevFakuraStatusOnStartUp");
        trigger.afterPropertiesSet();
        return trigger;
    }

}