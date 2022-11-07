package com.hanwha.tax.batch.total.repository;

import com.hanwha.tax.batch.entity.TotalIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface TotalIncomeRepository extends JpaRepository<TotalIncome, Long> {
    /**
     * 고객번호 기준으로 전체 수입정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_income tin where tin.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);

    /**
     * 고객번호 기준으로 마이데이터 수입정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_income tin where tin.cust_id=:custId and tin.flag_fk = 'M'", nativeQuery = true)
    int deleteByCustIdAndFlagFk(String custId);

    /**
     * 전체수입정보(간편장부+마이데이터) 기준일 변경내역 조회
     * @param ymdBasic
     * @return
     */
    @Query(value="select mi.id as fk, 'M' as flag_fk, mi.cust_id, YEAR(mi.trans_dtime) as `year`, MONTH(mi.trans_dtime) as `month`, mi.trans_amt as amount, CONCAT(mi.is_33,'') as is_33 from mydata_income mi where mi.is_income = 'Y' and COALESCE(mi.update_dt, mi.create_dt) like CONCAT(:ymdBasic,'%') union all " +
            "select bi.id as fk, 'B' as flag_fk, bi.cust_id, YEAR(bi.trans_dtime) as `year`, MONTH(bi.trans_dtime) as `month`, bi.trans_amt as amount, CONCAT(bi.is_33,'') as is_33 from book_income bi where COALESCE(bi.update_dt, bi.create_dt) like CONCAT(:ymdBasic,'%')", nativeQuery=true)
    List<Map<String,String>> getTotalIncomeTarget(String ymdBasic);

    /**
     * 외래키로 전체수입정보 조회
     * @param fk
     * @param flagFk
     * @return
     */
    List<TotalIncome> findByFkAndFlagFk(long fk, char flagFk);

    /**
     * 특정 연도 고객의 수입 총 금액 조회
     * @param custId
     * @param year
     * @return
     */
    @Query(value="select SUM(tin.amount) from total_income tin where tin.cust_id = :custId and tin.`year` = :year", nativeQuery=true)
    Long getTotalIncome(String custId, int year);

    /**
     * 특정 연도 고객의 3.3% 포함된 수입 총 금액 조회
     * @param custId
     * @param year
     * @return
     */
    @Query(value="select SUM(tin.amount) from total_income tin where tin.cust_id = :custId and tin.`year` = :year and tin.is_33 = 'Y'", nativeQuery=true)
    Long getTotalIncome33(String custId, int year);

    /**
     * 고객, 연도 별 전체 수입/지출정보 변경내역 조회
     * @param ymdBasic
     * @return
     */
    @Query(value="select tin.cust_id, tin.`year` from total_income tin where COALESCE(tin.update_dt, tin.create_dt) like CONCAT(:ymdBasic,'%') union " +
            "select tout.cust_id, tout.`year` from total_outgoing tout where COALESCE(tout.update_dt, tout.create_dt) like CONCAT(:ymdBasic,'%')", nativeQuery=true)
    List<Map<String,String>> getTotalChangeList(String ymdBasic);

    /**
     * 고객의 연도 별 전체수입정보 조회
     * @param custId
     * @return
     */
    @Query(value="select * from total_income tin where tin.cust_id = :custId group by tin.cust_id, tin.`year`", nativeQuery=true)
    List<TotalIncome> getTotalIncomeByCustId(String custId);

    /**
     * 전체(수입) 정보 삭제
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_income", nativeQuery = true)
    int deleteTotalIncome();

    /**
     * 쿠콘 마이데이터 월별 수입 금액 확인
     * @param custId
     * @param year
     * @param month
     * @param is33
     * @return
     */
    @Query(value="select SUM(tin.amount) as total, COUNT(*) as `count` from total_income tin where tin.flag_fk = 'M' and tin.cust_id = :custId and tin.`year` = :year and tin.`month` = :month and tin.is_33 = :is33", nativeQuery=true)
    Map<String, String> getTotalIncomeByMonth(String custId, int year, int month, char is33);
}