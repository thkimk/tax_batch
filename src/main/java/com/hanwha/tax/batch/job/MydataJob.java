package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.mydata.service.MydataService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@Slf4j
public class MydataJob extends BaseJob {

	private MydataService mydataService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		mydataService = (MydataService) SpringApplicationContext.getBean("mydataService");

		log.info("============= 마이데이터 배치 파일 저장 QUARTZ 시작 [{}] =============", Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

		mydataService.batchDataJob();

		log.info("============= 마이데이터 배치 파일 저장 QUARTZ 종료 [{}] =============", Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
	}
}
