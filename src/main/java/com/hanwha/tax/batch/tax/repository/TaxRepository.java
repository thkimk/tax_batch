package com.hanwha.tax.batch.tax.repository;

import com.hanwha.tax.batch.entity.Tax;
import com.hanwha.tax.batch.tax.model.TaxId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TaxRepository extends JpaRepository<Tax, TaxId> {
    /**
     * 고객번호 기준으로 소득세정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from tax t where t.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);

    /**
     * 소득세정보 일괄 삭제
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from tax", nativeQuery = true)
    int deleteTax();
}
