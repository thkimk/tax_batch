package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataIncome;

import java.util.List;

public interface MydataIncomeCustomRepository {
    List<MydataIncome> findByDataPk(MydataIncome mydataIncome);
}
