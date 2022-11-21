package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@Slf4j
public class DeductTransferJob extends AbstractBaseJob {

	private CustService custService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		custService = (CustService) SpringApplicationContext.getBean("custService");

		log.info("============= 고객 공제항목 승계 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		log.info("▶▶▶ ︎︎︎고객 공제항목 승계 대상 건수 : {} 건", custService.successionLastYearDeduct());

		log.info("============= 고객 공제항목 승계 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
