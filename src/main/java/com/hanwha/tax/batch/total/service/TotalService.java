package com.hanwha.tax.batch.total.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.entity.TotalIncome;
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

    /**
     * 기준일로 전체수입정보 등록 대상 리스트 조회
     * @param ymdBasic
     * @return
     */
    public List<Map<String,String>> getTotalIncomeTarget(String ymdBasic) {
        return totalIncomeRepository.getTotalIncomeTarget(ymdBasic);
    }

    /**
     * 기준일로 전체지출정보 등록 대상 리스트 조회
     * @param ymdBasic
     * @return
     */
    public List<Map<String,String>> getTotalOutgoingTarget(String ymdBasic) {
        return totalOutgoingRepository.getTotalOutgoingTarget(ymdBasic);
    }

    /**
     * 전체수입정보 저장
     * @param totalIncome
     * @return
     */
    public TotalIncome saveTotalIncome(TotalIncome totalIncome) {
        TotalIncome ti = totalIncomeRepository.findByFkAndFlagFk(totalIncome.getFk(), totalIncome.getFlagFk()).stream().findAny().orElse(null);

        if (ti != null) {
            totalIncome.setId(ti.getId());
            totalIncome.setCreateDt(ti.getCreateDt());
            totalIncome.setUpdateDt(Utils.getCurrentDateTime());
        }

        return totalIncomeRepository.save(totalIncome);
    }

    /**
     * 전체지출정보 저장
     * @param totalOutgoing
     * @return
     */
    public TotalOutgoing saveTotalOutgoing(TotalOutgoing totalOutgoing) {
        TotalOutgoing to = totalOutgoingRepository.findByFkAndFlagFk(totalOutgoing.getFk(), totalOutgoing.getFlagFk()).stream().findAny().orElse(null);

        if (to != null) {
            totalOutgoing.setId(to.getId());
            totalOutgoing.setCreateDt(to.getCreateDt());
            totalOutgoing.setUpdateDt(Utils.getCurrentDateTime());
        }

        return totalOutgoingRepository.save(totalOutgoing);
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
     * 고객의 연도 별 전체수입정보 조회
     * @param custId
     * @return
     */
    public List<TotalIncome> getTotalIncomeByCustId(String custId) {
        return totalIncomeRepository.getTotalIncomeByCustId(custId);
    }
}
