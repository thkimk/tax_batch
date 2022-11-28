package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import com.hanwha.tax.batch.entity.TotalIncome;
import com.hanwha.tax.batch.entity.TotalOutgoing;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.mydata.service.MydataService;
import com.hanwha.tax.batch.total.service.TotalService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.atomic.AtomicLong;


@Slf4j
public class TotalAmountJob extends AbstractBaseJob {

	private TotalService totalService;

	private MydataService mydataService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		totalService = (TotalService) SpringApplicationContext.getBean("totalService");
		mydataService = (MydataService) SpringApplicationContext.getBean("mydataService");

		log.info("============= 월 별 수입/지출 합계 정보 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		// 기준일 ( 당일 )
		String ymdBasic = Utils.getCurrentDate("yyyy-MM-dd");

		// 간편장부, 마이데이터 수입 변경내역 조회
		log.info("▶▶▶ TOTAL 수입정보 저장");
		totalService.getTotalIncomeTarget(ymdBasic).forEach(ti -> {
			// 전체수입정보
			TotalIncome totalIncome = new TotalIncome().convertByMydataMap(ti);

			// 총 수입금액 저장 ( 수입여부가 'Y'인 경우만 저장/'N'인 경우 삭제 )
			if ("Y".equals(ti.get("is_income"))) {
				totalService.saveTotalIncome(totalIncome);
			} else {
				totalService.deleteTotalIncomeByFkAndFlagFk(totalIncome.getFk(), totalIncome.getFlagFk());
			}
		});

		// 간편장부, 마이데이터 지출 변경내역 조회
		log.info("▶▶▶ TOTAL 지출정보 저장");
		totalService.getTotalOutgoingTarget(ymdBasic).forEach(to -> {
			// 전체지출정보
			TotalOutgoing totalOutgoing = new TotalOutgoing().convertByMydataMap(to);

			// 마이데이터 지출정보인 경우만 금액 계산
			if (!Utils.isEmpty(to.get("appr_num"))) {
				// 지출금액
				AtomicLong amount = new AtomicLong();

				// 마이데이터 승인번호 별 카드이력 조회
				mydataService.getMydataOutgoingByCardInfo(to.get("org_code"), to.get("card_id"), to.get("appr_num")).forEach(mo -> {
					// ★★★ 테스트 데이터 중복되어 코드 추가 함
					if (!mo.getCustId().equals(to.get("cust_id")))
						return;

					// 경비제외가 아닌 경우 지출금액 계산 ( ★★★ 원본 데이터 삽입 시 경비코드 빈값으로 셋팅됨 )
					if (Utils.isEmpty(mo.getCategory()) || MydataOutgoing.CardCategory.경비제외.getCode().equals(mo.getCategory()))
						return;

					// 승인, 승인취소, 정정에 따른 지출금액 계산
					if (MydataOutgoing.ApprStatus.승인.getCode().equals(mo.getStatus())) {
						amount.set(amount.get()+mo.getApprAmt());

						// 최초 승인내역 기준으로 외래키 세팅
						totalOutgoing.setFk(totalOutgoing.getFk() < mo.getId() ? totalOutgoing.getFk() : mo.getId());
					} else if (MydataOutgoing.ApprStatus.승인취소.getCode().equals(mo.getStatus())) {
						amount.set(amount.get()-mo.getApprAmt());
					} else {
						amount.set(mo.getModAmt());
					}
				});

				// 총 지출금액 세팅
				totalOutgoing.setAmount(amount.get());
			}

			// 총 지출금액 저장 ( 금액이 있는 경우만 저장/갱신하고 0원인 경우 삭제 )
			if (0 < totalOutgoing.getAmount()) {
				totalService.saveTotalOutgoing(totalOutgoing);
			} else {
				totalService.deleteTotalOutgoingByFkAndFlagFk(totalOutgoing.getFk(), totalOutgoing.getFlagFk());
			}
		});

		log.info("============= 월 별 수입/지출 합계 정보 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}