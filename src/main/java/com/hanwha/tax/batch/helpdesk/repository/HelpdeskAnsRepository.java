package com.hanwha.tax.batch.helpdesk.repository;

import com.hanwha.tax.batch.entity.HelpdeskAns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HelpdeskAnsRepository extends JpaRepository<HelpdeskAns, Long> {
    /**
     * 고객번호 기준으로 안내데스크 응답정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from helpdesk_ans ha where ha.helpdesk_id in (select id from helpdesk where cust_id=:custId)", nativeQuery = true)
    int deleteByCustId(String custId);
}
