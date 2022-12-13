package com.hanwha.tax.batch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.hanwha.tax.batch.fingerauto.*.repository", entityManagerFactoryRef = "fingerAutoEntityManager", transactionManagerRef = "fingerAutoTransactionManager")
public class FingerAutoDataSourceConfig {
	@Autowired
	private Environment env;

	@Bean
	public LocalContainerEntityManagerFactoryBean fingerAutoEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(fingerAutoDataSource());

		//Entity 패키지 경로
		em.setPackagesToScan(new String[] { "com.hanwha.tax.batch.fingerauto.entity" });

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);

		//Hibernate 설정
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto",env.getProperty("hibernate.hbm2ddl.auto"));
		properties.put("hibernate.dialect",env.getProperty("hibernate.dialect"));
		properties.put("hibernate.physical_naming_strategy","com.hanwha.tax.batch.config.hibernate.ImprovedNamingStrategy");
		em.setJpaPropertyMap(properties);
		return em;
	}

	@Bean
	@ConfigurationProperties(prefix="spring.finger-auto-ds")
	public DataSource fingerAutoDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public PlatformTransactionManager fingerAutoTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(fingerAutoEntityManager().getObject());
		return transactionManager;
	}
}
