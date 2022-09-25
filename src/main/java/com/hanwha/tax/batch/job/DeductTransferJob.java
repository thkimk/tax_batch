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
public class DeductTransferJob extends BaseJob {

	private CustService custService;

	private NoticeService noticeService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		custService = (CustService) SpringApplicationContext.getBean("custService");
		noticeService = (NoticeService) SpringApplicationContext.getBean("noticeService");

		log.info("============= 고객 공제항목 승계 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		log.info("============= 고객 공제항목 승계 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
