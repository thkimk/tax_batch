package com.hanwha.tax.batch;

import com.hanwha.tax.batch.model.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Slf4j
@PropertySource(value = {"classpath:tax.properties"})
public class BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
		log.info("## BatchApplication(): app starts....");
	}

	@Bean
	public SpringApplicationContext springApplicationContext() {
		SpringApplicationContext springApplicationContext = new SpringApplicationContext();
		return springApplicationContext;
	}



/*
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
*/
}
