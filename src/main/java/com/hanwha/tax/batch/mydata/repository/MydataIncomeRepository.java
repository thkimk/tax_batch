package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    /**
     * 은행(수입) 정보 삭제
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_income", nativeQuery = true)
    int deleteMydataIncome();

    /**
     * 은행(수입) 시퀀스 초기화
     */
    @Transactional
    @Modifying
    @Query(value="alter table mydata_income auto_increment = 1", nativeQuery = true)
    void resetSequenceMydataIncome();

    /**
     * 은행(수입) 중복내역 조회
     * @return
     */
    @Query(value="select row_number() over(partition by cust_id, org_code, account_num, seq_no, trans_dtime, trans_no, trans_type, trans_class, currency_code, trans_amt, balance_amt) as rn, mi.* " +
            "from mydata_income mi", nativeQuery=true)
    List<Map<String, String>> getMydataIncomeDuplicate();

    /**
     * 고객번호로 은행(수입) 내역 조회
     * @param custId
     * @return
     */
    List<MydataIncome> findByCustId(String custId);
}
