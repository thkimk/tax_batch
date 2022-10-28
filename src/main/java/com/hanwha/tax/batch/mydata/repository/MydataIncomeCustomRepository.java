package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataIncome;

import java.util.List;

public interface MydataIncomeCustomRepository {
    /**
     * 마이데이터 수입(은행) 중복 체크
     * @param mydataIncome
     * @return
     */
    List<MydataIncome> findByDataPk(MydataIncome mydataIncome);
}
