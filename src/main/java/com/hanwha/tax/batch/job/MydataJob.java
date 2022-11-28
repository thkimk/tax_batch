package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.mydata.service.MydataService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static com.hanwha.tax.batch.Constants.BANK_FILE;
import static com.hanwha.tax.batch.Constants.BANK_TRANS_FILE;
import static com.hanwha.tax.batch.Constants.CARD_APPR_FILE;
import static com.hanwha.tax.batch.Constants.CARD_FILE;
import static com.hanwha.tax.batch.Constants.REVOKE_FILE;
import static com.hanwha.tax.batch.Constants.THIRDPARTY_FILE;

@Slf4j
public class MydataJob extends AbstractBaseJob {

	private MydataService mydataService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		mydataService = (MydataService) SpringApplicationContext.getBean("mydataService");

		log.info("============= 마이데이터 배치 분석 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		String yesterday = Utils.getYesterday();

		log.info("▶︎▶︎▶ 마이데이터 쿠콘 탈회 확인");
		mydataService.procMydataInfo(REVOKE_FILE, yesterday);		// 쿠콘 탈회 파일 확인 ( 탈퇴를 제3자 제공동의보다 먼저 처리해야 함 )
		log.info("▶︎▶︎▶ 마이데이터 제3자 제공동의 확인");
		mydataService.procMydataInfo(THIRDPARTY_FILE, yesterday);	// 제3자 제공동의 파일 확인

		log.info("▶︎▶︎▶ 마이데이터 은행(원본) 확인");
		mydataService.procMydataInfo(BANK_FILE, yesterday);			// 은행(원본) 파일 확인
		log.info("▶︎▶︎▶ 마이데이터 카드(원본) 확인");
		mydataService.procMydataInfo(CARD_FILE, yesterday);			// 카드(원본) 파일 확인

		log.info("▶︎▶︎▶ 마이데이터 은행(수입) 확인");
		mydataService.procMydataInfo(BANK_TRANS_FILE, yesterday);	// 은행(수입) 파일 확인
		log.info("▶︎▶︎▶ 마이데이터 카드(경비) 확인");
		mydataService.procMydataInfo(CARD_APPR_FILE, yesterday);	// 카드(경비) 파일 확인

		log.info("============= 마이데이터 배치 분석 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
