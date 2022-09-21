package com.hanwha.tax.batch.tax.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.entity.Tax;
import com.hanwha.tax.batch.tax.model.TaxId;
import com.hanwha.tax.batch.tax.repository.TaxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("taxService")
public class TaxService {

    @Autowired
    TaxRepository taxRepository;

    public Tax saveTax(String custId, int year, long rateTax, long bookTax) {
        Tax tax = taxRepository.findById(new TaxId(custId, year)).orElse(null);

        if (tax == null) {
            tax = new Tax();
            tax.setCustId(custId);
            tax.setYear(year);
        } else {
            tax.setUpdateDt(Utils.getCurrentDateTime());
        }

        tax.setRateTax(rateTax);
        tax.setBookTax(bookTax);

        log.info("★★★ tax 정보 저장 [{}]", tax.toString());
        return taxRepository.save(tax);
    }
}
