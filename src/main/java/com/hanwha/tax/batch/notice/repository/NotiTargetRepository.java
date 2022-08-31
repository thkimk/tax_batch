package com.hanwha.tax.batch.notice.repository;

import com.hanwha.tax.batch.entity.NotiTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotiTargetRepository extends JpaRepository<NotiTarget, String> {
    /**
     * 고객번호 기준으로 알람대상 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from noti_target nt where nt.cust_id=:custId", nativeQuery = true)
    void deleteById(String custId);
}
