package com.hanwha.tax.batch.total.repository;

import com.hanwha.tax.batch.entity.TotalOutgoing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TotalOutgoingRepository extends JpaRepository<TotalOutgoing, Long> {
    /**
     * 고객번호 기준으로 전체 경비 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_outgoing tout where tout.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);

    @Query(value="select 0 as id, lst.cust_id, year(lst.appr_dtime), month(lst.appr_dtime), lst.appr_amt as amount, lst.category " +
            "from ( " +
                "select mo.*, ROW_NUMBER() over(PARTITION BY cust_id, card_id, appr_num ORDER BY trans_dtime desc, seq desc) as rn " +
                "from mydata_outgoing mo where (mo.create_dt like concat(:ymdBasic,'%') or mo.update_dt like concat(:ymdBasic,'%')) ) lst " +
            "where lst.rn = 1 union all " +
            "select 0 as id, cust_id, appr_amt, appr_dtime, merchant_name, category from book_outgoing bo where (bo.create_dt like concat(:ymdBasic,'%') or bo.update_dt like concat(:ymdBasic,'%'))", nativeQuery=true)
    List<TotalOutgoing> getTotalOutgoingTarget(String ymdBasic);

    @Query(value="select sum(ti.amount) from (" +
            "select mo.appr_amt as amount from mydata_outgoing mo where mo.cust_id=:custId and year(mo.appr_dtime)=:year union all " +
            "select bo.appr_amt as amount from book_outgoing bo where bo.cust_id=:custId and year(bo.appr_dtime)=:year) ti", nativeQuery=true)
    Long getTotalOutgoing(String custId, int year);

    @Query(value="select sum(ti.amount) from (" +
            "select mo.appr_amt as amount from mydata_outgoing mo where mo.cust_id=:custId and year(mo.appr_dtime)=:year and month(mo.appr_dtime)=:month union all " +
            "select bo.appr_amt as amount from book_outgoing bo where bo.cust_id=:custId and year(bo.appr_dtime)=:year and month(bo.appr_dtime)=:month) ti", nativeQuery=true)
    Long getTotalOutgoingMonth(String custId, int year, int month);
}
