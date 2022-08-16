package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.mydata.model.MydataIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MydataRepository extends JpaRepository<MydataIncome, Long> {
    List<MydataIncome> findByCustIdAndOrgCodeAndAccountNumAndTransDtime(String cust_id, String org_code, String account_num, String trans_dtime);
}
