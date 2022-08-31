package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.entity.CustFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustFamilyRepository extends JpaRepository<CustFamily, Long> {
    /**
     * 고객번호 기준으로 고객가족정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from cust_family cf where cf.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}
