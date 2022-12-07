package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.mydata.service.MydataService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;


@Slf4j
public class CustDestroyWithdrawalJob extends AbstractBaseJob {

	private CustService custService;

	private MydataService mydataService;

	@Value("${tax.api.domain}")
	private String domainApi;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		custService = (CustService) SpringApplicationContext.getBean("custService");
		mydataService = (MydataService) SpringApplicationContext.getBean("mydataService");

		log.info("============= 탈퇴회원 파기 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		// 1. 회원 틸퇴 후 30일 지난 고객 대상 확인 ( 회원탈퇴 후 30일 지난 고객리스트 )
		List<Cust> custAsctOutList = custService.getCustAsctOutList();      // 준회원 파기 대상리스트
		List<Cust> custRegOutList = custService.getCustRegOutist();         // 정회원 파기 대상리스트

		// 2. 고객 관련 정보 순차적으로 삭제
		try {
			// 2-1. 준회원 데이터 삭제
			log.info("▶▶▶ ︎︎︎준회원 파기 대상 건수 : {} 건", custAsctOutList.size());
			for (final Cust cust :  custAsctOutList) {
				custService.destroyCust(cust.getCustId());
			}

			// 2-2. 정회원 데이터 삭제
			log.info("▶▶▶ 정회원 파기 대상 건수 : {} 건", custAsctOutList.size());
			for (final Cust cust :  custRegOutList) {
				// 2-2-1. 고객정보 파기
				custService.destroyCust(cust.getCustId());

				// 2-2-2. 제3자 제공동의 철회
				mydataService.revoke(cust.getCustId());
			}
		} catch(Exception e) {
			log.error("※※※ Exception : {}", e);
		}

		log.info("============= 탈퇴회원 파기 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
