package com.hanwha.tax.batch.auth.service;

import com.hanwha.tax.batch.auth.repository.AuthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("authService")
public class AuthService {

    @Autowired
    AuthRepository authRepository;

    /**
     * ci값으로 고객번호 조회하기
     * @param ci
     * @return
     */
    public String getCustIdByCi(String ci) {
        return authRepository.findByCi(ci).stream().findAny().orElse(null).getCustId();
    }
}
