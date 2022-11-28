package com.hanwha.tax.batch.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Slf4j
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanFactory;

    public void setApplicationContext(final ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) {
        final Object job;
        try {
            job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
        } catch (Exception e) {
            log.error("**** 요청 처리 실패 {}", e);
            return null;
        }

        return job;
    }

}
