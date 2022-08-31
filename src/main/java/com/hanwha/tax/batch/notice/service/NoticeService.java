package com.hanwha.tax.batch.notice.service;

import com.hanwha.tax.batch.notice.repository.NotiSettingRepository;
import com.hanwha.tax.batch.notice.repository.NotiTargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("noticeService")
public class NoticeService {

    @Autowired
    NotiSettingRepository notiSettingRepository;

    @Autowired
    NotiTargetRepository notiTargetRepository;

    /**
     * 고객번호로 알람설정 삭제
     * @param custId
     * @return
     */
    public void deleteNotiSettingById(String custId) {
        notiSettingRepository.deleteById(custId);
    }

    /**
     * 고객번호로 알람대상 삭제
     * @param custId
     * @return
     */
    public void deleteNotiTargetById(String custId) {
        notiTargetRepository.deleteById(custId);
    }
}
