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
//		mydataService.resetMydata();

		int startDate = 20221023;
		String yesterday = Utils.getYesterday();

		for (int i = startDate; i <= Integer.parseInt(yesterday); i++) {
			if (i == 20221032)	i = 20221101;
			if (i == 20221131)	i = 20221201;

			log.info("▶︎▶︎▶ 마이데이터 은행(원본) 확인 [{}]", String.valueOf(i));
			mydataService.procMydataInfo(BANK_FILE, String.valueOf(i));			// 은행(원본) 파일 확인
			log.info("▶︎▶︎▶ 마이데이터 카드(원본) 확인 [{}]", String.valueOf(i));
			mydataService.procMydataInfo(CARD_FILE, String.valueOf(i));			// 카드(원본) 파일 확인

			log.info("▶︎▶︎▶ 마이데이터 은행(수입) 확인 [{}]", String.valueOf(i));
			mydataService.procMydataInfo(BANK_TRANS_FILE, String.valueOf(i));	// 은행(수입) 파일 확인
			if (20221101 < i) {
				log.info("▶︎▶︎▶ 마이데이터 카드(경비) 확인 [{}]", String.valueOf(i));
				mydataService.procMydataInfo(CARD_APPR_FILE, String.valueOf(i));	// 카드(경비) 파일 확인
			}
		}

		// 중복 값 조회
		log.info("▶▶▶ 마이데이터 은행(수입) 중복 내역");
		mydataService.getMydataIncomeDuplicate().forEach(mi -> {
			if (!"1".equals(String.valueOf(mi.get("rn"))))
				log.info("[custId={},orgCode={},accountNum={},seqNo={},transDtime={},transNo={},seq={},transAmt={}]", mi.get("cust_id"), mi.get("org_code"), mi.get("account_num"), mi.get("seq_no"), mi.get("trans_dtime"), mi.get("trans_no"), mi.get("seq"), mi.get("trans_amt"));
		});
		log.info("▶▶▶ 마이데이터 카드(경비) 중복 내역");
		mydataService.getMydataOutgoingDuplicate().forEach(mo -> {
			if (!"1".equals(String.valueOf(mo.get("rn")))) {
				log.info("[custId={},orgCode={},cardId={},seq={},transDtime={},apprNum={},apprAmt={},apprDtime={}]", mo.get("cust_id"), mo.get("org_code"), mo.get("card_id"), mo.get("seq"), mo.get("trans_dtime"), mo.get("appr_num"), mo.get("appr_amt"), mo.get("appr_dtime"));
			}
		});

		// 수입/경비 내역 조회
		String custId = "2210576542";
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
