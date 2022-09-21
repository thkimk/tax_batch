package com.hanwha.tax.batch.total.service;

import com.hanwha.tax.batch.entity.TotalOutgoing;
import com.hanwha.tax.batch.total.repository.TotalIncomeRepository;
import com.hanwha.tax.batch.total.repository.TotalOutgoingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service("totalService")
public class TotalService {

    @Autowired
    TotalIncomeRepository totalIncomeRepository;

    @Autowired
    TotalOutgoingRepository totalOutgoingRepository;

    /**
     * 회원번호로 전체 수입 삭제
     * @param custId
     * @return
     */
    public int deleteTotalIncomeByCustId(String custId) {
        return totalIncomeRepository.deleteByCustId(custId);
    }

    /**
     * 회원번호로 전체 경비 삭제
     * @param custId
     * @return
     */
    public int deleteTotalOutgoingByCustId(String custId) {
        return totalOutgoingRepository.deleteByCustId(custId);
    }

    public List<TotalOutgoing> getTotalOutgoingTarget(String ymdBasic) {
        return totalOutgoingRepository.getTotalOutgoingTarget(ymdBasic);
    }

    /**
     * 전체 수입 조회
     * @param custId
     * @param year
     * @return
     */
    public Long getTotalIncome(String custId, int year) {
        return totalIncomeRepository.getTotalIncome(custId, year);
    }

    /**
     * 전체 지출 조회
     * @param custId
     * @param year
     * @return
     */
    public Long getTotalOutgoing(String custId, int year) {
        return totalOutgoingRepository.getTotalOutgoing(custId, year);
    }

    /**
     * 전체 3.3% 수입 조회
     * @param custId
     * @param year
     * @return
     */
    public Long getTotalIncome33(String custId, int year) {
        return totalIncomeRepository.getTotalIncome33(custId, year);
    }

    /**
     * 기준일에 변경된 수입/지출 내역 조회
     * @param ymdBasic
     * @return
     */
    public List<Map<String,String>> getTotalChangeList(String ymdBasic) {
        return totalIncomeRepository.getTotalChangeList(ymdBasic);
    }

    /**
     * 회원의
     * @param custId
     * @return
     */
    public List<Map<String,String>> getTotalIncomeList(String custId) {
        return totalIncomeRepository.getTotalIncomeList(custId);
    }

    /**
     * 회원의 월별 전체 지출금액
     * @param custId
     * @return
     */
    public TotalOutgoing saveTotalOutgoing(String custId, int year, int month) {
        return totalOutgoingRepository.save(new TotalOutgoing(custId, year, month, totalOutgoingRepository.getTotalOutgoingMonth(custId, year, month)));
    }
}
