package com.hanwha.tax.batch.tax.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.entity.Tax;
import com.hanwha.tax.batch.entity.Tax2;
import com.hanwha.tax.batch.tax.model.TaxId;
import com.hanwha.tax.batch.tax.repository.Tax2Repository;
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

    @Autowired
    Tax2Repository tax2Repository;

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

    public Tax2 saveTax2(String custId, int year, CalcTax calcTax) {
        Tax2 tax = tax2Repository.findById(new TaxId(custId, year)).orElse(null);

        if (tax == null) {
            tax = new Tax2();
            tax.setCustId(custId);
            tax.setYear(year);
        } else {
            tax.setUpdateDt(Utils.getCurrentDateTime());
        }

        if (calcTax != null) {
            // 경비율 기반 소득세 및 공제금액 세팅
            calcTax.init(custId, year);
            tax.setRateTax(calcTax.calRateTax());
            tax.setIncome(calcTax.getIncome());
            tax.setRateOutgo(calcTax.getOutgoing());
            tax.setRateMyDeduct(calcTax.getDeductMe());
            tax.setRateFamilyDeduct(calcTax.getDeductFamily());
            tax.setRateOtherDeduct(calcTax.getDeductOthers());
            tax.setRateIraDeduct(0);

            // 간편장부 기반 소득세 및 공제금액 세팅
            calcTax.init(custId, year);
            tax.setBookTax(calcTax.calBookTax());
            tax.setBookOutgo(calcTax.getOutgoing());
            tax.setBookMyDeduct(calcTax.getDeductMe());
            tax.setBookFamilyDeduct(calcTax.getDeductFamily());
            tax.setBookOtherDeduct(calcTax.getDeductOthers());
            tax.setBookIraDeduct(0);
        }

        log.info("★★★ tax2 정보 저장 [{}]", tax.toString());
        return tax2Repository.save(tax);
    }
}
