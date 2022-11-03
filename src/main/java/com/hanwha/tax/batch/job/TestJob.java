package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.mydata.service.MydataService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static com.hanwha.tax.batch.Constants.*;


@Slf4j
public class TestJob extends BaseJob {

	private MydataService mydataService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		mydataService = (MydataService) SpringApplicationContext.getBean("mydataService");

		log.info("============= QUARTZ 테스트 시작 [{}] =============", Utils.getCurrentDateTime());

		// 기존 정보 삭제
		mydataService.resetMydata();

//		int startDate = 20221023;
//		String yesterday = Utils.getYesterday();
//
//		for (int i = 20221101; i <= Integer.parseInt(yesterday); i++) {
//			if (i == 20221032)	i = 20221101;
//			if (i == 20221131)	i = 20221201;
//
//			log.info("▶︎▶︎▶ 마이데이터 은행(원본) 확인 [{}]", String.valueOf(i));
//			mydataService.procMydataInfo(BANK_FILE, String.valueOf(i));			// 은행(원본) 파일 확인
//			log.info("▶︎▶︎▶ 마이데이터 카드(원본) 확인 [{}]", String.valueOf(i));
//			mydataService.procMydataInfo(CARD_FILE, String.valueOf(i));			// 카드(원본) 파일 확인
//
//			log.info("▶︎▶︎▶ 마이데이터 은행(수입) 확인 [{}]", String.valueOf(i));
//			mydataService.procMydataInfo(BANK_TRANS_FILE, String.valueOf(i));	// 은행(수입) 파일 확인
//			log.info("▶︎▶︎▶ 마이데이터 카드(경비) 확인 [{}]", String.valueOf(i));
//			mydataService.procMydataInfo(CARD_APPR_FILE, String.valueOf(i));	// 카드(경비) 파일 확인
//		}

		// 중복 값 조회
		log.info("▶▶▶ 마이데이터 은행(수입) 중복 내역");
		mydataService.getMydataIncomeDuplicate().forEach(mi -> {
			log.info("{}", mi.toString());
		});
		log.info("▶▶▶ 마이데이터 카드(경비) 중복 내역");
		mydataService.getMydataOutgoingDuplicate().forEach(mo -> {
			log.info("{}", mo.toString());
		});

		// 수입/경비 내역 조회
		String custId = "";
		log.info("▶▶▶ 마이데이터 카드(경비) My내역");
		mydataService.getMydataIncomeByCustId(custId).forEach(mi -> {
			log.info("{}", mi.toString());
		});
		log.info("▶▶▶ 마이데이터 카드(경비) My내역");
		mydataService.getMydataOutgoingByCustId(custId).forEach(mo -> {
			log.info("{}", mo.toString());
		});

		log.info("============= QUARTZ 테스트 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
