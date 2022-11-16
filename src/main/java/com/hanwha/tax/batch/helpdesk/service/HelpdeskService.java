package com.hanwha.tax.batch.helpdesk.service;

import com.hanwha.tax.batch.helpdesk.repository.HelpdeskAnsRepository;
import com.hanwha.tax.batch.helpdesk.repository.HelpdeskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("helpdeskService")
public class HelpdeskService {

    @Autowired
    private HelpdeskRepository helpdeskRepository;

    @Autowired
    private HelpdeskAnsRepository helpdeskAnsRepository;

    /**
     * 고객번호로 안내데스크 삭제
     * @param custId
     * @return
     */
    public int deleteHelpdeskByCustId(String custId) {
        return helpdeskRepository.deleteByCustId(custId);
    }

    /**
     * 고객번호로 안내데스크 응답정보 삭제
     * @param custId
     * @return
     */
    public int deleteHelpdeskAnsByCustId(String custId) {
        return helpdeskAnsRepository.deleteByCustId(custId);
    }
}
