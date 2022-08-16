package com.hanwha.tax.batch.auth.repository;

import com.hanwha.tax.batch.auth.model.AuthId;
import com.hanwha.tax.batch.auth.model.AuthInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthRepository extends JpaRepository<AuthInfo, AuthId> {
    /**
     * ci값으로 고객번호 조회
     * @param ci
     * @return
     */
    List<AuthInfo> findByCi(String ci);
}
