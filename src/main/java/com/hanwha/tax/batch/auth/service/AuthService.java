package com.hanwha.tax.batch.auth.service;

import com.hanwha.tax.batch.auth.repository.AuthInfoRepository;
import com.hanwha.tax.batch.entity.AuthInfo;
import com.hanwha.tax.batch.entity.Cust;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("authService")
public class AuthService {

    @Autowired
    AuthInfoRepository authInfoRepository;

    /**
     * ci값으로 고객번호 조회
     * @param ci
     * @return
     */
    public String getCustIdByCi(String ci) {
        return authInfoRepository.findByCi(ci).stream().findAny().orElse(new AuthInfo()).getCustId();
    }

    /**
     * 고객번호로 인증정보 삭제
     * @param custId
     * @return
     */
    public int deleteAuthByCustId(String custId) {
        return authInfoRepository.deleteByCustId(custId);
    }
}
