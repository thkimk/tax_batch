package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@Slf4j
public class CustStatJob extends AbstractBaseJob {

	private CustService custService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		custService = (CustService) SpringApplicationContext.getBean("custService");

		log.info("============= 일별 고객 이용 현황 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		String basicYmd = Utils.getCurrentDate();
		if (custService.saveCustStat(basicYmd) == null) {
			log.error("▶︎︎︎ [{}] 고객 등급 현황 저장 실패", basicYmd);
		}

		log.info("============= 일별 고객 이용 현황 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
