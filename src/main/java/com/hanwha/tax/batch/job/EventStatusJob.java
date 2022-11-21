package com.hanwha.tax.batch.job;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.event.service.EventService;
import com.hanwha.tax.batch.model.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@Slf4j
public class EventStatusJob extends AbstractBaseJob {

	private EventService eventService;

    @Override
	protected void doExecute(JobExecutionContext context) throws JobExecutionException {
		eventService = (EventService) SpringApplicationContext.getBean("eventService");

		log.info("============= 이벤트 참여 인원수 갱신 QUARTZ 시작 [{}] =============", Utils.getCurrentDateTime());

		eventService.updateEventJoinCnt();

		log.info("============= 이벤트 참여 인원수 갱신 QUARTZ 종료 [{}] =============", Utils.getCurrentDateTime());
	}
}
