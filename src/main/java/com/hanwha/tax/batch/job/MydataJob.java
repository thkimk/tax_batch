package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.mydata.service.MydataService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static com.hanwha.tax.batch.Constants.BANK_TRANS_FILE;
import static com.hanwha.tax.batch.Constants.CARD_APPR_FILE;


@Slf4j
public class MydataJob extends BaseJob {

	private MydataService mydataService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		mydataService = (MydataService) SpringApplicationContext.getBean("mydataService");

		log.info("============= 마이데이터 배치 파일 저장 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		String yesterday = Utils.getYesterday();
		mydataService.procMydataInfo(BANK_TRANS_FILE, yesterday);	// 은행(수입) 파일 확인
		mydataService.procMydataInfo(CARD_APPR_FILE, yesterday);	// 카드(경비) 파일 확인

		log.info("============= 마이데이터 배치 파일 저장 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
