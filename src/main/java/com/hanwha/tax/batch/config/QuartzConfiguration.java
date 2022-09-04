package com.hanwha.tax.batch.config;

import com.hanwha.tax.batch.job.CustDestroyDormancyJob;
import com.hanwha.tax.batch.job.CustDestroyWithdrawalJob;
import com.hanwha.tax.batch.job.CustDormancyJob;
import com.hanwha.tax.batch.job.MydataJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;

@Configuration
public class QuartzConfiguration {

	@Value("${tax.mydata.schedule}")
	private String mydataCronExp;

	@Value("${tax.cust.destroy.withdrawal.schedule}")
	private String custDestroyWithdrawalCronExp;

	@Value("${tax.cust.destroy.dormancy.schedule}")
	private String custDestroyDormancyCronExp;

	@Value("${tax.cust.dormancy.schedule}")
	private String custDormancyCronExp;

	private final String TRIGGER_GROUP_NAME = "TAX_GROUP";


	@Autowired
	private ApplicationContext appContext;
	
	@Bean(name="mydataJobDetail")
	public JobDetailFactoryBean mydataJobDetail() {
	    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
	    jobDetailFactory.setJobClass(MydataJob.class);
	    jobDetailFactory.setDescription("Collect mydata_income and mydata_outgoing Data");
	    jobDetailFactory.setDurability(true);
	    return jobDetailFactory;
	}

	@Bean(name="mydataTrigger")
	public CronTriggerFactoryBean mydataTrigger(JobDetail mydataJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setGroup(TRIGGER_GROUP_NAME);
		trigger.setCronExpression(mydataCronExp);
		trigger.setJobDetail(mydataJobDetail);

		return trigger;
	}

	@Bean(name="custDestroyWithdrawalJobDetail")
	public JobDetailFactoryBean custDestroyWithdrawalJobDetail() {
		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(CustDestroyWithdrawalJob.class);
		jobDetailFactory.setDescription("Destroy Cust Withdrawal Data");
		jobDetailFactory.setDurability(true);
		return jobDetailFactory;
	}

	@Bean(name="custDestroyWithdrawalTrigger")
	public CronTriggerFactoryBean custDestroyWithdrawalTrigger(JobDetail custDestroyWithdrawalJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setGroup(TRIGGER_GROUP_NAME);
		trigger.setCronExpression(custDestroyWithdrawalCronExp);
		trigger.setJobDetail(custDestroyWithdrawalJobDetail);

		return trigger;
	}

	@Bean(name="custDestroyDormancyJobDetail")
	public JobDetailFactoryBean custDestroyDormancyJobDetail() {
		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(CustDestroyDormancyJob.class);
		jobDetailFactory.setDescription("Destroy Cust Dormancy Data");
		jobDetailFactory.setDurability(true);
		return jobDetailFactory;
	}

	@Bean(name="custDestroyDormancyTrigger")
	public CronTriggerFactoryBean custDestroyDormancyTrigger(JobDetail custDestroyDormancyJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setGroup(TRIGGER_GROUP_NAME);
		trigger.setCronExpression(custDestroyDormancyCronExp);
		trigger.setJobDetail(custDestroyDormancyJobDetail);

		return trigger;
	}

	@Bean(name="custDormancyJobDetail")
	public JobDetailFactoryBean custDormancyJobDetail() {
		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(CustDormancyJob.class);
		jobDetailFactory.setDescription("cust Dormancy");
		jobDetailFactory.setDurability(true);
		return jobDetailFactory;
	}

	@Bean(name="custDormancyTrigger")
	public CronTriggerFactoryBean custDormancyTrigger(JobDetail custDormancyJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setGroup(TRIGGER_GROUP_NAME);
		trigger.setCronExpression(custDormancyCronExp);
		trigger.setJobDetail(custDormancyJobDetail);

		return trigger;
	}


	@Bean
	public SpringBeanJobFactory springBeanJobFactory() {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
	    jobFactory.setApplicationContext(appContext);
	    
	    return jobFactory;
	}
	
	@Bean(name = "quartzDatasource")
    @ConfigurationProperties(prefix = "spring.quartz-ds.datasource")
    public DataSource quartzDataSource() {
		return DataSourceBuilder.create().build();
    }
	
	@Bean
	public SchedulerFactoryBean scheduler(Trigger[] triggers) throws Exception {
	    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
	    schedulerFactory.setOverwriteExistingJobs(true);
	    schedulerFactory.setDataSource(quartzDataSource());
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz/quartz.properties"));
	    schedulerFactory.setJobFactory(springBeanJobFactory());
	    schedulerFactory.setTriggers(triggers);
	    schedulerFactory.setSchedulerName("TAX-scheduler");
	    return schedulerFactory;
	}
}