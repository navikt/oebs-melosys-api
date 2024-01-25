package no.nav.oebs.melosys.api.common.quartz;

import no.nav.oebs.melosys.kafka.ScheduledFakturaStatusProducer;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.sql.SQLException;
import java.util.Date;

@Configuration
@EnableAutoConfiguration
@ConditionalOnExpression("'${using.spring.schedulerFactory}'=='true'")
public class SpringQuartzScheduler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        logger.info("Hello world from Spring...");
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
                                          DataSource dataSource) throws SQLException {
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

        return schedulerFactory;
    }

    @Bean(name = "LevFakturaStatus")
    public JobDetailFactoryBean levFakturaStatus() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(ScheduledFakturaStatusProducer.class);
        jobDetailFactory.setName("Qrtz_LevFakturaStatus_kafka");
        jobDetailFactory.setDescription("Start skedulert kafka producer for fakturastatus...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean fakturaStatusTrigger(@Qualifier("LevFakturaStatus") JobDetail job){
        int frequencyInsec = 60;
        Date startTime = DateBuilder.todayAt(8, 30, 0);
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setStartTime(startTime);
        trigger.setRepeatInterval(frequencyInsec * 1000 * 5);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Qrtz_Trigger_LevFakuraStatus");
        return trigger;
    }

    @Bean
    public SimpleTriggerFactoryBean fakturaStatusTriggerOnStartup(@Qualifier("LevFakturaStatus") JobDetail job){
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setStartDelay(0L);
        trigger.setRepeatCount(0);
        trigger.setName("Qrtz_Trigger_LevFakuraStatusOnStartUp");
        trigger.afterPropertiesSet();
        return trigger;
    }

}
