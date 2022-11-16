package com.hanwha.tax.batch.terms.service;

import com.hanwha.tax.batch.terms.repository.TermsHstRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("termsService")
public class TermsService {

    @Autowired
    private TermsHstRepository termsHstRepository;

    /**
     * 고객번호로 약관동의이력 삭제
     * @param custId
     * @return
     */
    public int deleteTermsHstByCustId(String custId) {
        return termsHstRepository.deleteByCustId(custId);
    }
}
