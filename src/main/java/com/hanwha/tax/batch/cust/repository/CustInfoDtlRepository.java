package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.entity.CustInfoDtl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustInfoDtlRepository extends JpaRepository<CustInfoDtl, String> {
    /**
     * 고객번호 기준으로 고객상세정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from cust_info_dtl cid where cid.cust_id=:custId", nativeQuery = true)
    void deleteById(String custId);
}
