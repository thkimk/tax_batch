package com.hanwha.tax.batch.total.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import com.hanwha.tax.batch.entity.TotalIncome;
import com.hanwha.tax.batch.entity.TotalOutgoing;
import com.hanwha.tax.batch.mydata.service.MydataService;
import com.hanwha.tax.batch.total.repository.TotalIncomeRepository;
import com.hanwha.tax.batch.total.repository.TotalOutgoingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service("totalService")
public class TotalService {

    @Autowired
    private TotalIncomeRepository totalIncomeRepository;

    @Autowired
    private TotalOutgoingRepository totalOutgoingRepository;

    @Autowired
    private MydataService mydataService;

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
     * 회원번호로 마이데이터 수입 삭제
     * @param custId
     * @return
     */
    public int deleteTotalIncomeByCustIdAndFlagFk(String custId) {
        return totalIncomeRepository.deleteByCustIdAndFlagFk(custId);
    }

    /**
     * 회원번호로 마이데이터 경비 삭제
     * @param custId
     * @return
     */
    public int deleteTotalOutgoingByCustIdAndFlagFk(String custId) {
        return totalOutgoingRepository.deleteByCustIdAndFlagFk(custId);
    }

    /**
     * 외래키로 마이데이터 수입 삭제
     * @param fk
     * @param flagFk
     * @return
     */
    public int deleteTotalIncomeByFkAndFlagFk(long fk, char flagFk) {
        return totalIncomeRepository.deleteByFkAndFlagFk(fk, flagFk);
    }

    /**
     * 외래키로 마이데이터 경비 삭제
     * @param fk
     * @param flagFk
     * @return
     */
    public int deleteTotalOutgoingByFkAndFlagFk(long fk, char flagFk) {
        return totalOutgoingRepository.deleteByFkAndFlagFk(fk, flagFk);
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

        // 3.3% 미포함인 경우 포함금액으로 계산
        if (totalIncome.getIs33() != null && totalIncome.getIs33() == 'N') {
            totalIncome.setAmount(totalIncome.getAmount()*1000/967);
        }

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
    public long getTotalIncome(String custId, int year) {
        Long income = totalIncomeRepository.getTotalIncome(custId, year);

        return income == null ? 0 : income;
    }

    /**
     * 전체 지출 조회
     * @param custId
     * @param year
     * @return
     */
    public long getTotalOutgoing(String custId, int year) {
        Long outgoing = totalOutgoingRepository.getTotalOutgoing(custId, year);

        return outgoing == null ? 0 : outgoing;
    }

    /**
     * 전체 3.3% 수입 조회
     * @param custId
     * @param year
     * @return
     */
    public long getTotalIncome33(String custId, int year) {
        Long income33 = totalIncomeRepository.getTotalIncome33(custId, year);

        return income33 == null ? 0 : income33;
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

    /**
     * 월별 total 수입 금액/건수 조회
     * @param custId
     * @param year
     * @param month
     * @param is33
     * @return
     */
    public Map<String,String> getTotalIncomeByMonth(String custId, long year, long month, char is33) {
        return totalIncomeRepository.getTotalIncomeByMonth(custId, year, month, is33);
    }

    /**
     * 월별 total 경비 금액/건수 조회
     * @param custId
     * @param year
     * @param month
     * @param category
     * @return
     */
    public Map<String,String> getTotalOutgoingByMonth(String custId, long year, long month, String category) {
        return totalOutgoingRepository.getTotalOutgoingByMonth(custId, year, month, category);
    }

    /**
     * 총 수입/지출 금액 배치 처리
     * @param ymdBasic
     */
    public void procTotalAmountJob(String ymdBasic) {
        // 간편장부, 마이데이터 수입 변경내역 조회
        log.info("▶▶▶ TOTAL 수입정보 저장");
        getTotalIncomeTarget(ymdBasic).forEach(ti -> {
            // 전체수입정보
            TotalIncome totalIncome = new TotalIncome().convertByMydataMap(ti);

            // 총 수입금액 저장 ( 수입여부가 'Y'인 경우만 저장/'N'인 경우 삭제 )
            if ("Y".equals(ti.get("is_income"))) {
                saveTotalIncome(totalIncome);
            } else {
                deleteTotalIncomeByFkAndFlagFk(totalIncome.getFk(), totalIncome.getFlagFk());
            }
        });

        // 간편장부, 마이데이터 지출 변경내역 조회
        log.info("▶▶▶ TOTAL 지출정보 저장");
        getTotalOutgoingTarget(ymdBasic).forEach(to -> {
            // 전체지출정보
            TotalOutgoing totalOutgoing = new TotalOutgoing().convertByMydataMap(to);

            // 마이데이터 지출정보인 경우만 금액 계산
            if (!Utils.isEmpty(to.get("appr_num"))) {
                // 지출금액
                AtomicLong amount = new AtomicLong();

                // 마이데이터 승인번호 별 카드이력 조회
                mydataService.getMydataOutgoingByCardInfo(to.get("org_code"), to.get("card_id"), to.get("appr_num")).forEach(mo -> {
                    // ★★★ 테스트 데이터 중복되어 코드 추가 함
                    if (!mo.getCustId().equals(to.get("cust_id")))
                        return;

                    // 경비제외가 아닌 경우 지출금액 계산 ( ★★★ 원본 데이터 삽입 시 경비코드 빈값으로 셋팅됨 )
                    if (Utils.isEmpty(mo.getCategory()) || MydataOutgoing.CardCategory.경비제외.getCode().equals(mo.getCategory()))
                        return;

                    // 승인, 승인취소, 정정에 따른 지출금액 계산
                    if (MydataOutgoing.ApprStatus.승인.getCode().equals(mo.getStatus())) {
                        amount.set(amount.get()+mo.getApprAmt());

                        // 최초 승인내역 기준으로 외래키 세팅
                        totalOutgoing.setFk(totalOutgoing.getFk() < mo.getId() ? totalOutgoing.getFk() : mo.getId());
                    } else if (MydataOutgoing.ApprStatus.승인취소.getCode().equals(mo.getStatus())) {
                        amount.set(amount.get()-mo.getApprAmt());
                    } else {
                        amount.set(mo.getModAmt());
                    }
                });

                // 총 지출금액 세팅
                totalOutgoing.setAmount(amount.get());
            }

            // 총 지출금액 저장 ( 금액이 있는 경우만 저장/갱신하고 0원인 경우 삭제 )
            if (0 < totalOutgoing.getAmount()) {
                saveTotalOutgoing(totalOutgoing);
            } else {
                deleteTotalOutgoingByFkAndFlagFk(totalOutgoing.getFk(), totalOutgoing.getFlagFk());
            }
        });
    }
}