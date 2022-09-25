package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.Cust;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import com.hanwha.tax.batch.tax.service.CalcTax;
import com.hanwha.tax.batch.tax.service.TaxService;
import com.hanwha.tax.batch.total.service.TotalService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class TaxAllmembersJob extends BaseJob {

    private CustService custService;
    private TotalService totalService;
    private TaxService taxService;
    private CalcTax calcTax;

    @Override
    protected void doExecute(JobExecutionContext context) throws JobExecutionException {
        custService = (CustService) SpringApplicationContext.getBean("custService");
        totalService = (TotalService) SpringApplicationContext.getBean("totalService");
        taxService = (TaxService) SpringApplicationContext.getBean("taxService");
        calcTax = (CalcTax) SpringApplicationContext.getBean("calcTax");

        log.info("============= 고객 예상 소득세 계산 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

        // 기준일 ( 어제일자 )
        String ymdBasic = Utils.getYesterday("yyyy-MM-dd");

        // 전체 수입/지출 변경이력 조회 ( 수입/지출 뿐만 아니라 ★★★직종이 변경되는 경우 소득세 결과가 달라질 수 있음 )
        totalService.getTotalChangeList(ymdBasic).forEach(t -> {
            int year = Integer.parseInt(String.valueOf(t.get("year")));

            log.info("▶︎▶︎▶︎ 회원번호 [{}] 연도 [{}]", t.get("cust_id"), year);

            // 소득세 계산 시 필요한 기본정보 세팅
            calcTax.init(t.get("cust_id"), year);

            // 경비율, 간편장부 소득세 계산하여 소득세 저장
            taxService.saveTax(t.get("cust_id"), year, calcTax.calRateTax(), calcTax.calBookTax());
        });

//        // 정상 상태의 전체 고객리스트 조회
//        custService.getCustListByStatus(Cust.CustStatus.정상.getCode()).forEach(c -> {
//            AtomicBoolean ynIncome = new AtomicBoolean(false);
//
//            // 해당 고객의 연도 별 거래이력 조회
//            totalService.getTotalIncomeList(c.getCustId()).forEach(t -> {
//                ynIncome.set(true);
//                int year = Integer.parseInt(String.valueOf(t.get("year")));
//
//                log.info("▶︎▶︎▶︎ 회원번호 [{}] 연도 [{}]", t.get("cust_id"), year);
//
//                // 소득세 계산 시 필요한 기본정보 세팅
//                calcTax.init(t.get("cust_id"), year);
//
//                // 경비율, 간편장부 소득세 계산하여 소득세 저장
//                taxService.saveTax(c.getCustId(), year, calcTax.calRateTax(), calcTax.calBookTax());
//            });
//
//            if (!ynIncome.get()) {
//                taxService.saveTax(c.getCustId(), Integer.parseInt(Utils.getCurrentDate("yyyy")), 0, 0);
//            }
//        });

        log.info("============= 고객 예상 소득세 계산 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
    }
}