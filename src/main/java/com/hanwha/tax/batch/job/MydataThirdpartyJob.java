package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.mydata.service.MydataService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@Slf4j
public class MydataThirdpartyJob extends BaseJob {

	private MydataService mydataService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		mydataService = (MydataService) SpringApplicationContext.getBean("mydataService");

		log.info("============= 마이데이터 제3자 제공동의 확인 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		mydataService.checkThirdpartyDataJob();

		log.info("============= 마이데이터 제3자 제공동의 확인 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
