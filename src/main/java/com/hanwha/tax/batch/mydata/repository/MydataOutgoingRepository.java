package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataIncome;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface MydataOutgoingRepository extends JpaRepository<MydataOutgoing, Long>, MydataOutgoingCustomRepository {
    /**
     * 고객번호 기준으로 마이데이터 경비정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_outgoing mo where mo.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);

    /**
     * 카드정보로 특정 카드이력 내역 조회
     * @param orgCode
     * @param carId
     * @param apprNum
     * @return
     */
    List<MydataOutgoing> findByOrgCodeAndCardIdAndApprNumOrderByTransDtimeAscSeqAsc(String orgCode, String carId, String apprNum);

    /**
     * 카드(경비) 테이블 초기화
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_outgoing", nativeQuery = true)
    int deleteMydataOutgoing();

    /**
     * 카드(경비) 중복내역 조회
     * @return
     */
    @Query(value="select row_number() over(partition by cust_id, org_code, card_id, appr_num, appr_dtime, status, pay_type, trans_dtime, appr_amt) as rn, mo.* " +
            "from mydata_outgoing mo", nativeQuery=true)
    List<Map<String, String>> getMydataOutgoingDuplicate();

    /**
     * 고객번호로 카드(지출) 내역 조회
     * @param custId
     * @return
     */
    List<MydataOutgoing> findByCustId(String custId);
}
