package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.HttpUtil;
import com.hanwha.tax.batch.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;


@Slf4j
public class HealthCheckJob extends AbstractBaseJob {

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		log.info("============= HealthCheck QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());
		String targetUrl = "http://localhost:8080/actuator/healthcheck";

		HttpUtil.sendReqGETJson(targetUrl, new HashMap<>());

		log.info("============= HealthCheck QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
