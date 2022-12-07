package com.hanwha.tax.batch.fingerauto.repository;

import com.hanwha.tax.batch.entity.finger.MailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MailHistoryRepository extends JpaRepository<MailHistory, Long> {
    /**
     * 고객번호 기준으로 로그인이력 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from login_hst lh where lh.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}