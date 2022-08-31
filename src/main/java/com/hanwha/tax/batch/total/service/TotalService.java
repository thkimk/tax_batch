package com.hanwha.tax.batch.total.service;

import com.hanwha.tax.batch.total.repository.TotalIncomeRepository;
import com.hanwha.tax.batch.total.repository.TotalOutgoingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("totalService")
public class TotalService {

    @Autowired
    TotalIncomeRepository totalIncomeRepository;

    @Autowired
    TotalOutgoingRepository totalOutgoingRepository;

    /**
     * 고객번호로 전체 수입 삭제
     * @param custId
     * @return
     */
    public int deleteTotalIncomeByCustId(String custId) {
        return totalIncomeRepository.deleteByCustId(custId);
    }

    /**
     * 고객번호로 전체 경비 삭제
     * @param custId
     * @return
     */
    public int deleteTotalOutgoingByCustId(String custId) {
        return totalOutgoingRepository.deleteByCustId(custId);
    }
}
