package com.hanwha.tax.batch.event.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.cust.service.CustService;
import com.hanwha.tax.batch.entity.EventStatus;
import com.hanwha.tax.batch.event.model.EventStatusId;
import com.hanwha.tax.batch.event.repository.EventRepository;
import com.hanwha.tax.batch.event.repository.EventStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("eventService")
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventStatusRepository eventStatusRepository;

    @Autowired
    CustService custService;

    /**
     * 이벤트 현황 참여 인원수 업데이트
     */
    public void updateEventJoinCnt() {
        // 어제일자
        String yesterday = Utils.getYesterday("yyyy-MM-dd");

        // 현재 진행중인 이벤트 리스트 조회
        eventRepository.getEventListOngoing(yesterday).forEach(e -> {
            // 이벤트 현황 조회
            EventStatus eventStatus = eventStatusRepository.findById(new EventStatusId(e.getId(),yesterday)).orElse(null);

            // 이벤트 현황 정보가 없을 경우
            if (eventStatus == null) {
                eventStatus = new EventStatus();
                eventStatus.setEventId(e.getId());
                eventStatus.setDay(yesterday);
            }

            // 이벤트 참여 인원수 조회
            eventStatus.setJoinCnt(custService.getCntCustEventApply(e.getId(), 'Y', yesterday));

            // 이벤트 현황 저장
            log.info("▶︎▶︎▶︎ 현재 진행 이벤트 : [{}], 참여 인원수 : [{}]", eventStatus.getEventId(), eventStatus.getJoinCnt());
            eventStatusRepository.save(eventStatus);
        });
    }
}
