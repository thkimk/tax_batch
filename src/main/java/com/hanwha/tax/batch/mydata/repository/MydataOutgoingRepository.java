package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataOutgoing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MydataOutgoingRepository extends JpaRepository<MydataOutgoing, Long>, MydataOutgoingCustomRepository {
    /**
     * 고객번호 기준으로 마이데이터 경비정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_outgoing mo where mo.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}
