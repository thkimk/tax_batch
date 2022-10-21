package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.cust.model.CustDeductId;
import com.hanwha.tax.batch.entity.CustDeduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustDeductRepository extends JpaRepository<CustDeduct, CustDeductId> {
    /**
     * 고객번호 기준으로 고객자산정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from cust_deduct cd where cd.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);

    /**
     * 회원의 직전년도,당해년도 수입 조회
     * @param custId
     * @param year
     * @return
     */
    @Query(value = "select cd.income from cust_deduct cd where cd.cust_id = :custId and cd.year <= :year order by cd.year desc limit 2", nativeQuery = true)
    Long[] getCustIncomes(String custId, int year);

    /**
     * 고객 공제항목 승계
     * @return
     */
    @Transactional
    @Modifying
    @Query(value = "insert into cust_deduct (cust_id, `year`, income, outgoing, med_amt, npc_amt, rsp_amt, sed_amt, ira_amt, irp_amt, is_rsp, is_smal, is_med, is_sed, is_ira, create_dt) " +
            "select cust_id, year(now()) as `year`, income, outgoing, med_amt, npc_amt, rsp_amt, sed_amt, ira_amt, irp_amt, is_rsp, is_smal, is_med, is_sed, is_ira, now() " +
            "from cust_deduct cd where cd.cust_id not in (select cust_id from cust_deduct where `year` = year(NOW())) and `year` = year(now())-1", nativeQuery = true)
    int successionLastYearDeduct();
}
