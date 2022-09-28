package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.cust.model.CustEventId;
import com.hanwha.tax.batch.entity.CustEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustEventRepository extends JpaRepository<CustEvent, CustEventId> {
    /**
     * 고객번호 기준으로 고객 이벤트정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from cust_event ce where ce.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);

    /**
     * 이벤트의 참여건수 조회
     * @param eventId
     * @param result
     * @param joinDt
     * @return
     */
    int countByEventIdAndResultAndJoinDtContains(String eventId, char result, String joinDt);
}
