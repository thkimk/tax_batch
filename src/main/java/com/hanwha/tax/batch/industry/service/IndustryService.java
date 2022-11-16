package com.hanwha.tax.batch.industry.service;

import com.hanwha.tax.batch.entity.Industry;
import com.hanwha.tax.batch.industry.repository.IndustryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("industryService")
public class IndustryService {

    @Autowired
    private IndustryRepository industryRepository;

    /**
     * 업종코드로 업종정보 조회
     * @param code
     * @return
     */
    public Optional<Industry> getIndustry(String code) {
        return industryRepository.findById(code);
    }
}
