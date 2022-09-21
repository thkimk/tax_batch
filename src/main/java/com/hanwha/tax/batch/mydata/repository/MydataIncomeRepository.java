package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MydataIncomeRepository extends JpaRepository<MydataIncome, Long>, MydataIncomeCustomRepository {
    /**
     * 고객번호 기준으로 마이데이터 수입정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_income mi where mi.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}
