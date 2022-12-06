package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.*;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.notice.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;


@Slf4j
public class NotiTargetJob extends AbstractBaseJob {

	private CustService custService;

	private NoticeService noticeService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		custService = (CustService) SpringApplicationContext.getBean("custService");
		noticeService = (NoticeService) SpringApplicationContext.getBean("noticeService");

		log.info("============= 알람 대상 고객 정보 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());
		
		// 정상 상태의 준회원, 정회원 고객리스트 조회하여 알람 정보 입력
		custService.getCustListByStatus(Cust.CustStatus.정상.getCode()).forEach(c -> {
			// 고객 관련 정보 조회
			CustInfo custInfo = custService.getCustInfo(c.getCustId()).orElse(null);
			CustInfoDtl custInfoDtl = custService.getCustInfoDtl(c.getCustId()).orElse(null);
			CustDeduct custDeduct = custService.getCustDeduct(c.getCustId(), Integer.parseInt(Utils.getCurrentDate("yyyy"))).orElse(null);
			List<CustFamily> custFamilyList = custService.getCustFamilyListByCustId(c.getCustId());

			// 고객정보를 알람정보로 변환
			NotiTarget notiTarget = new NotiTarget().convertByCust(c, custInfo, custInfoDtl, custDeduct, custFamilyList);

			// 고객번호, 이메일 검증
			if (Utils.isEmpty(notiTarget.getCustId()) || Utils.isEmpty(notiTarget.getEmail())) {
				log.error("※※※ 알람 대상 고객정보를 확인해 주시기 바랍니다. [회원번호 : {}]", notiTarget.getCustId());
				return;
			}

			// 알람 대상 정보 저장
			if (noticeService.saveNotiTarget(notiTarget) == null) {
				log.error("※※※ 알람 대상 고객정보 저장에 실패하였습니다. [회원번호 : {}]", notiTarget.getCustId());
			}
		});

		log.info("============= 알람 대상 고객 정보 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
