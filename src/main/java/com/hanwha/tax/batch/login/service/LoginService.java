package com.hanwha.tax.batch.login.service;

import com.hanwha.tax.batch.login.repository.LoginHstRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("loginService")
public class LoginService {

    @Autowired
    private LoginHstRepository loginHstRepository;

    /**
     * 고객번호로 로그인이력 삭제
     * @param custId
     * @return
     */
    public int deleteLoginHstByCustId(String custId) {
        return loginHstRepository.deleteByCustId(custId);
    }
}
