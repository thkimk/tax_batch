package com.hanwha.tax.batch.total.repository;

import com.hanwha.tax.batch.entity.TotalOutgoing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    /**
     * 고객번호 기준으로 마이데이터 경비 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_outgoing tout where tout.cust_id=:custId and tout.flag_fk = 'M'", nativeQuery = true)
    int deleteByCustIdAndFlagFk(String custId);

    /**
     * 외래키 기준으로 경비 삭제
     * @param fk
     * @param flagFk
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_outgoing tout where tout.fk=:fk and tout.flag_fk = :flagFk", nativeQuery = true)
    int deleteByFkAndFlagFk(long fk, char flagFk);

    /**
     * 전체지출정보(간편장부+마이데이터) 기준일 변경내역 조회
     * @param ymdBasic
     * @return
     */
    @Query(value="select lst.id as fk, 'M' as flag_fk, lst.cust_id, lst.org_code, lst.card_id, lst.appr_num, YEAR(lst.appr_dtime) as `year`, MONTH(lst.appr_dtime) as `month`, (case when lst.status = '03' then lst.mod_amt else lst.appr_amt end) as amount, lst.category " +
            "from ( " +
            "select mo.*, ROW_NUMBER() over(PARTITION BY cust_id, org_code, card_id, appr_num ORDER BY trans_dtime desc, seq desc) as rn " +
            "from mydata_outgoing mo where COALESCE(mo.update_dt, mo.create_dt) like CONCAT(:ymdBasic,'%') ) lst " +
            "where lst.rn = 1 union all " +
            "select bo.id as fk, 'B' as flag_fk, bo.cust_id, null as org_code, null as card_id, null as appr_num, YEAR(bo.appr_dtime) as `year`, MONTH(bo.appr_dtime) as `month`, bo.appr_amt as amount, bo.category from book_outgoing bo where COALESCE(bo.update_dt, bo.create_dt) like CONCAT(:ymdBasic,'%')", nativeQuery=true)
    List<Map<String,String>> getTotalOutgoingTarget(String ymdBasic);

    /**
     * 외래키로 전체지출정보 조회
     * @param fk
     * @param flagFk
     * @return
     */
    List<TotalOutgoing> findByFkAndFlagFk(long fk, char flagFk);

    /**
     * 특정 연도 고객의 지출 총 금액 조회
     * @param custId
     * @param year
     * @return
     */
    @Query(value="select SUM(tout.amount) from total_outgoing tout where tout.cust_id = :custId and tout.`year` = :year", nativeQuery=true)
    Long getTotalOutgoing(String custId, int year);

    /**
     * 전체(경비) 테이블 초기화
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from total_outgoing", nativeQuery = true)
    int deleteTotalOutgoing();

    /**
     * 전체(경비) 시퀀스 초기화
     */
    @Transactional
    @Modifying
    @Query(value="alter table total_outgoing auto_increment = 1", nativeQuery = true)
    void resetSequenceTotalOutgoing();
}