package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.cust.model.CustDeductId;
import com.hanwha.tax.batch.entity.CustDeduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
}
