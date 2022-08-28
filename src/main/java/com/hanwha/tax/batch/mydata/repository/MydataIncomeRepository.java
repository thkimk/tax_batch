package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataIncome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MydataIncomeRepository extends JpaRepository<MydataIncome, Long>, MydataIncomeCustomRepository {
}
