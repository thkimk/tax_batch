package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class TaxAllmembersJob extends BaseJob {

    @Override
    protected void doExecute(JobExecutionContext context) throws JobExecutionException {
        log.info("============= 고객 예상 소득세 계산 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

        log.info("============= 고객 예상 소득세 계산 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
    }
}
