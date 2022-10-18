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

        String isMaster = context.getJobDetail().getJobDataMap().getString("isMaster");

        log.info("============= 고객 예상 소득세 계산 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());
        log.info("▶︎▶︎▶︎ isMaster : [{}]", isMaster);

        if ("true".equals(isMaster)) {
            // 정상 상태의 전체 고객리스트 조회
            custService.getCustListByStatus(Cust.CustStatus.정상.getCode()).forEach(c -> {
                AtomicBoolean ynIncome = new AtomicBoolean(false);

                // 해당 고객의 연도 별 수입이력 조회
                totalService.getTotalIncomeByCustId(c.getCustId()).forEach(t -> {
                    ynIncome.set(true);

                    // 소득세 계산 및 저장
                    procTax(t.getCustId(), String.valueOf(t.getYear()));
                });

                if (!ynIncome.get()) {
                    taxService.saveTax(c.getCustId(), Integer.parseInt(Utils.getCurrentDate("yyyy")), null);
                }
            });
        } else {
            // 기준일 ( 당일 )
            String ymdBasic = Utils.getCurrentDate("yyyy-MM-dd");

            // 전체 수입/지출 변경이력 조회 ( 수입/지출 뿐만 아니라 ★★★직종이 변경되는 경우 소득세 결과가 달라질 수 있음 )
            totalService.getTotalChangeList(ymdBasic).forEach(t -> {
                // 소득세 계산 및 저장
                procTax(t.get("cust_id"), String.valueOf(t.get("year")));
            });
        }

        log.info("============= 고객 예상 소득세 계산 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
    }

    /**
     * 고객 별 소득세 계산하여 저장
     * @param custId
     * @param yearStr
     */
    private void procTax(String custId, String yearStr) {
        int year = Integer.parseInt(yearStr);

        log.info("▶︎▶︎▶︎ 회원번호 : [{}] 연도 : [{}]", custId, year);

        // 경비율, 간편장부 소득세 계산하여 소득세 저장
        taxService.saveTax(custId, year, calcTax);
    }
}