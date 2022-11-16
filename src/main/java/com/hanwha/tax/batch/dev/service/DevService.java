package com.hanwha.tax.batch.dev.service;

import com.hanwha.tax.batch.dev.repository.DevRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("devService")
public class DevService {

    @Autowired
    private DevRepository devRepository;

    /**
     * 고객번호로 단말기정보 삭제
     * @param custId
     * @return
     */
    public void deleteDevInfoById(String custId) {
        devRepository.deleteById(custId);
    }
}
