package com.hanwha.tax.batch.tax.service;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.entity.Tax;
import com.hanwha.tax.batch.tax.model.TaxId;
import com.hanwha.tax.batch.tax.repository.TaxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("taxService")
public class TaxService {

    @Autowired
    private TaxRepository taxRepository;

    public Tax saveTax(String custId, int year, CalcTax calcTax) {
        Tax tax = taxRepository.findById(new TaxId(custId, year)).orElse(null);

        if (tax == null) {
            tax = new Tax();
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
            tax.setRateIraDeduct(calcTax.getTaxDeductIra());

            // 간편장부 기반 소득세 및 공제금액 세팅
            calcTax.init(custId, year);
            tax.setBookTax(calcTax.calBookTax());
            tax.setBookOutgo(calcTax.getOutgoing());
            tax.setBookMyDeduct(calcTax.getDeductMe());
            tax.setBookFamilyDeduct(calcTax.getDeductFamily());
            tax.setBookOtherDeduct(calcTax.getDeductOthers());
            tax.setBookIraDeduct(calcTax.getTaxDeductIra());
        }

        return taxRepository.save(tax);
    }

    /**
     * 고객번호로 소득세정보 삭제
     * @param custId
     * @return
     */
    public int deleteTaxByCustId(String custId) {
        return taxRepository.deleteByCustId(custId);
    }
}
