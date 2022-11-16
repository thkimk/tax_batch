package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;


@Slf4j
public class CustDestroyDormancyJob extends BaseJob {

	private CustService custService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		custService = (CustService) SpringApplicationContext.getBean("custService");

		log.info("============= 휴면회원 파기 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		// 1. 최종 이용 후 5년이 지난 고객 (휴면회원) 대상 확인
		List<Cust> custDormancyList = custService.getCustDormancyListByYmdBasic(Utils.addYears(Utils.getCurrentDate("yyyy-MM-dd"),-5)+" 23:59:59", Cust.CustStatus.휴면.getCode());

		// 2. 고객 관련 정보 순차적으로 삭제
		try {
			log.info("▶▶▶ 휴면회원 파기 대상 건수 : {} 건", custDormancyList.size());
			for (Cust cust :  custDormancyList) {
				custService.deleteCustData(cust.getCustId());
			}
		} catch(Exception e) {
			log.error("※※※ Exception : {}", e);
		}

		log.info("============= 휴면회원 파기 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
