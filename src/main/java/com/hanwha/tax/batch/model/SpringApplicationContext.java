package com.hanwha.tax.batch.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring 이 기동될때 처음 생성한 ApplicationContext 를 참조하도록 한다.
 */
public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext CONTEXT;
	
	private Logger logger = LoggerFactory.getLogger(SpringApplicationContext.class);

	public SpringApplicationContext() {
		logger.info("init SpringApplicationContext");
	}

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		CONTEXT = context;
	}

	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}

	public static <T> T getBean(String beanName, Class<T> requiredType) {
		return CONTEXT.getBean(beanName, requiredType);
	}

}
