package no.nav.oebs.melosys.api.common.quartz;

import no.nav.oebs.melosys.api.vieri.konteringsinfohbvieri.v1.VieriKontHbSkedulertJobb;
import no.nav.oebs.melosys.api.vieri.konteringsinfovieri.v1.VieriKontSkedulertJobb;
import no.nav.oebs.melosys.api.vieri.leverandorinfovieri.v1.VieriLevSkedulertJobb;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

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
    public SchedulerFactoryBean scheduler(Map<String, JobDetail> jobMap,
                                          Map<String, Trigger> triggers) {
                                          // DataSource quartzDataSource) {

        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        logger.debug("Setter opp skedulering ...");
        schedulerFactory.setJobFactory(springBeanJobFactory());

//        Utils.printAllJobDetails(jobMap);
//        Utils.printAllTriggers(triggers);

        JobDetail[] jobs = jobMap.values().toArray(new JobDetail[0]);
        System.out.println(Arrays.toString(jobs));
        Trigger[] tr = triggers.values().toArray(new Trigger[0]);
        System.out.println(Arrays.toString(tr));

        schedulerFactory.setJobDetails(jobs);
        schedulerFactory.setTriggers(tr);

        // Comment the following line to use the default Quartz job store.
        // schedulerFactory.setDataSource(quartzDataSource);

        return schedulerFactory;
    }


    @Bean(name = "LevJobb")
    public JobDetailFactoryBean levJobb() {

        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(VieriLevSkedulertJobb.class);
        jobDetailFactory.setName("Qrtz_LevJobb_Detaljer");
        jobDetailFactory.setDescription("Start leverandør skedulert jobbtjeneste...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean levTrigger(@Qualifier("LevJobb") JobDetail job) {

        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);

        int frequencyInSec = 3600;
        logger.info("Leverandør triggeren starter hver {} minutter", frequencyInSec);

        trigger.setStartTime(new Date(System.currentTimeMillis() + 120000));
        trigger.setRepeatInterval(frequencyInSec * 1000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Qrtz_Trigger_LevJobb");
        return trigger;
    }

    @Bean(name = "kontJobb")
    public JobDetailFactoryBean kontJobb() {

        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(VieriKontSkedulertJobb.class);
        jobDetailFactory.setName("Qrtz_KontJobb_Detaljer");
        jobDetailFactory.setDescription("Start konteringsinfo skedulert jobbtjeneste...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean kontTrigger(@Qualifier("kontJobb") JobDetail job) {

        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);

        int frequencyInSec = 3600;
        logger.info("Konteringsinfo triggeren starter hver {} minutter", frequencyInSec);

        trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));
        trigger.setRepeatInterval(frequencyInSec * 1000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Qrtz_Trigger_KontJobb");
        return trigger;
    }

    @Bean(name = "kontHbJobb")
    public JobDetailFactoryBean kontHbJobb() {

        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(VieriKontHbSkedulertJobb.class);
        jobDetailFactory.setName("Qrtz_KontHbJobb_Detaljer");
        jobDetailFactory.setDescription("Start konteringsinfo hovedbok skedulert jobbtjeneste...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean kontHbTrigger(@Qualifier("kontHbJobb") JobDetail job) {

        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);

        int frequencyInSec = 3600;
        logger.info("Konteringsinfo hovedbok triggeren starter hver {} minutter", frequencyInSec);

        trigger.setStartTime(new Date(System.currentTimeMillis() + 240000));
        trigger.setRepeatInterval(frequencyInSec * 1000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setName("Qrtz_Trigger_KontHbJobb");
        return trigger;
    }
    /*@Bean
    @QuartzDataSource
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create().build();
    }*/

}
