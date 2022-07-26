package com.hanwha.tax.batch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(basePackages = "com.hanwha.tax.batch.*.repository", entityManagerFactoryRef = "masterEntityManager", transactionManagerRef = "masterTransactionManager")
public class MainDataSourceConfig {
	@Autowired
	private Environment env;

	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean masterEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(masterDataSource());

		//Entity 패키지 경로
		em.setPackagesToScan(new String[] { "com.hanwha.tax.batch.entity" });

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

	@Primary
	@Bean
	@ConfigurationProperties(prefix="spring.datasource")
	public DataSource masterDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean
	public PlatformTransactionManager masterTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(masterEntityManager().getObject());
		return transactionManager;
	}
}
