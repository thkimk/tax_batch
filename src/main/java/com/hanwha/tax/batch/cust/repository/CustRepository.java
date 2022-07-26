package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.entity.Cust;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CustRepository extends JpaRepository<Cust, String> {
    /**
     * 고객상태, 고객등급, 준회원탈퇴일시로 준회원 탈퇴 리스트 조회
     * @param custStatus
     * @param custGrade
     * @param asctOutDt
     * @return
     */
    List<Cust> findByCustStatusAndCustGradeAndAsctOutDtLessThan(String custStatus, String custGrade, String asctOutDt);

    /**
     * 고객상태, 고객등급, 정회원탈퇴일시로 정회원 탈퇴 리스트 조회
     * @param custStatus
     * @param custGrade
     * @param regOutDt
     * @return
     */
    List<Cust> findByCustStatusAndCustGradeAndRegOutDtLessThan(String custStatus, String custGrade, String regOutDt);

    /**
     * 고객정보 파기
     * @param custId
     */
    @Transactional
    @Modifying
    @Query(value="call proc_deleteCust(:custId)", nativeQuery = true)
    void destroyCust(String custId);

    /**
     * 고객번호 기준으로 고객정보 삭제
     * @param custId
     */
    @Transactional
    @Modifying
    @Query(value="delete from cust c where c.cust_id=:custId", nativeQuery = true)
    void deleteById(String custId);

    /**
     * 휴면기간과 고객상태로 휴면회원 리스트 조회
     * @param ymdBasic
     * @param custStatus
     * @return
     */
    @Query(value="select * from cust c where (select COUNT(*) from login_hst lh where c.cust_id = lh.cust_id and lh.login_dt > :ymdBasic) = 0 and c.cust_status = :custStatus", nativeQuery = true)
    List<Cust> getCustDormancyList(String ymdBasic, String custStatus);

    /**
     * 회원 상태로 회원리스트 조회
     * @param custStatus
     * @return
     */
    List<Cust> findByCustStatus(String custStatus);

    /**
     * 회원 상태/등급으로 회원리스트 조회
     * @param custStatus
     * @param custGrade
     * @return
     */
    List<Cust> findByCustStatusAndCustGrade(String custStatus, String custGrade);
}
