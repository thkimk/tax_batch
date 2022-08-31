package com.hanwha.tax.batch.total.repository;

import com.hanwha.tax.batch.entity.TotalOutgoing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TotalOutgoingRepository extends JpaRepository<TotalOutgoing, Long> {
    /**
     * 고객번호 기준으로 전체 경비 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_outgoing tout where tout.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}
