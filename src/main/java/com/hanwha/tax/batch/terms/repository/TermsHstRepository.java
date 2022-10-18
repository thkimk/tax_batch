package com.hanwha.tax.batch.terms.repository;

import com.hanwha.tax.batch.entity.TermsHst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TermsHstRepository extends JpaRepository<TermsHst, Long> {
    /**
     * 고객번호 기준으로 약관동의이력 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from terms_hst th where th.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}