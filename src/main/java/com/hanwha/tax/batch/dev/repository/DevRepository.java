package com.hanwha.tax.batch.dev.repository;

import com.hanwha.tax.batch.entity.DevInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DevRepository extends JpaRepository<DevInfo, String> {
    /**
     * 고객번호 기준으로 단말기정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from dev_info di where di.cust_id=:custId", nativeQuery = true)
    void deleteById(String custId);
}
