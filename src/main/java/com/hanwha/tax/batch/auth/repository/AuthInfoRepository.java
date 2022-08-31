package com.hanwha.tax.batch.auth.repository;

import com.hanwha.tax.batch.auth.model.AuthId;
import com.hanwha.tax.batch.entity.AuthInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AuthInfoRepository extends JpaRepository<AuthInfo, AuthId> {
    /**
     * ci값으로 고객번호 조회
     * @param ci
     * @return
     */
    List<AuthInfo> findByCi(String ci);

    /**
     * 고객번호 기준으로 인증정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from auth_info ai where ai.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}
