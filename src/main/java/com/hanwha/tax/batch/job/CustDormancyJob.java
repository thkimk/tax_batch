package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@Slf4j
public class CustDormancyJob extends AbstractBaseJob {

	private CustService custService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		custService = (CustService) SpringApplicationContext.getBean("custService");

		log.info("============= 고객 휴면전환 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		custService.custDormancyBatch();

		log.info("============= 고객 휴면전환 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
