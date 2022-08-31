package com.hanwha.tax.batch.helpdesk.repository;

import com.hanwha.tax.batch.entity.Helpdesk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HelpdeskRepository extends JpaRepository<Helpdesk, Long> {
    /**
     * 고객번호 기준으로 안내데스크 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from helpdesk h where h.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}