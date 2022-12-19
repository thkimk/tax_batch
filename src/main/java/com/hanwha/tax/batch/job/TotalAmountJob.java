package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.total.service.TotalService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@Slf4j
public class TotalAmountJob extends AbstractBaseJob {

	private TotalService totalService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		totalService = (TotalService) SpringApplicationContext.getBean("totalService");

		log.info("============= 월 별 수입/지출 합계 정보 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		// 당일 기준 총 수입/지출 금액 계산
		totalService.procTotalAmountJob(Utils.getCurrentDate("yyyy-MM-dd"));

		log.info("============= 월 별 수입/지출 합계 정보 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}