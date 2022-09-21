package com.hanwha.tax.batch.total.repository;

import com.hanwha.tax.batch.entity.TotalIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface TotalIncomeRepository extends JpaRepository<TotalIncome, Long> {
    /**
     * 고객번호 기준으로 전체 수입 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_income ti where ti.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);

    @Query(value="select sum(ti.amount) from (" +
            "select mi.trans_amt as amount from mydata_income mi where mi.cust_id=:custId and mi.is_income = 'Y' and year(mi.trans_dtime)=:year union all " +
            "select bi.trans_amt as amount from book_income bi where bi.cust_id=:custId and year(bi.trans_dtime)=:year) ti", nativeQuery=true)
    Long getTotalIncome(String custId, int year);

    @Query(value="select sum(ti.amount) from (" +
            "select mi.trans_amt as amount from mydata_income mi where mi.cust_id=:custId and mi.is_income = 'Y' and year(mi.trans_dtime)=:year and month(mi.trans_dtime)=:month union all " +
            "select bi.trans_amt as amount from book_income bi where bi.cust_id=:custId and year(bi.trans_dtime)=:year and month(bi.trans_dtime)=:month) ti", nativeQuery=true)
    Long getTotalIncomeMonth(String custId, int year, int month);

    @Query(value="select sum(ti.amount) from (" +
            "select mi.trans_amt as amount from mydata_income mi where mi.cust_id=:custId and mi.is_income = 'Y' and year(mi.trans_dtime)=:year and mi.is_33='Y' union all " +
            "select bi.trans_amt as amount from book_income bi where bi.cust_id=:custId and year(bi.trans_dtime)=:year and bi.is_33='Y') ti", nativeQuery=true)
    Long getTotalIncome33(String custId, int year);

    @Query(value="select mi.cust_id, YEAR(trans_dtime) as year from mydata_income mi where (mi.create_dt like concat(:ymdBasic,'%') or mi.update_dt like concat(:ymdBasic,'%')) and mi.is_income='Y' union " +
            "select bi.cust_id, YEAR(trans_dtime) as year from book_income bi where (bi.create_dt like concat(:ymdBasic,'%') or bi.update_dt like concat(:ymdBasic,'%')) union " +
            "select mo.cust_id, YEAR(trans_dtime) as year from mydata_outgoing mo where (mo.create_dt like concat(:ymdBasic,'%') or mo.update_dt like concat(:ymdBasic,'%')) union " +
            "select bo.cust_id, YEAR(appr_dtime) as year from book_outgoing bo where (bo.create_dt like concat(:ymdBasic,'%') or bo.update_dt like concat(:ymdBasic,'%'))", nativeQuery=true)
    List<Map<String,String>> getTotalChangeList(String ymdBasic);

    @Query(value="select mi.cust_id, year(mi.trans_dtime) as year from mydata_income mi where mi.cust_id=:custId and mi.is_income = 'Y' union " +
            "select bi.cust_id, year(bi.trans_dtime) as year from book_income bi where bi.cust_id=:custId", nativeQuery=true)
    List<Map<String,String>> getTotalIncomeList(String custId);
}
