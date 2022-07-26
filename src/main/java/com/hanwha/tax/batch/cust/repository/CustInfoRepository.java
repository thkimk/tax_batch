package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.entity.CustInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustInfoRepository extends JpaRepository<CustInfo, String> {
    /**
     * 고객번호 기준으로 고객정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from cust_info ci where ci.cust_id=:custId", nativeQuery = true)
    void deleteById(String custId);
}
