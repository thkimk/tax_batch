package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.cust.model.CustTermsAgmtId;
import com.hanwha.tax.batch.entity.CustTermsAgmt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustTermsAgmtRepository extends JpaRepository<CustTermsAgmt, CustTermsAgmtId> {
    /**
     * 고객번호 기준으로 고객약관동의 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from cust_terms_agmt cta where cta.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}
