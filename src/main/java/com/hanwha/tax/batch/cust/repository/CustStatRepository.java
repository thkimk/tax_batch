package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.entity.CustStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustStatRepository extends JpaRepository<CustStat, String> {
    /**
     * 기준일 별 고객 이용 현황 정보 계산
     * @param basicYmd
     * @return
     */
    @Query(value="select 'downCnt' as col, count(*) as cnt from cust_nomember cn where DATE_FORMAT(create_dt,'%Y%m%d') = :basicYmd union " +
            "select 'loginCnt' as col, COUNT(*) as cnt from login_hst lh where DATE_FORMAT(login_dt,'%Y%m%d') = :basicYmd and auth_status = 'LOGIN' union " +
            "select 'newCnt' as col, COUNT(*) as cnt from (select * from login_hst lh where DATE_FORMAT(login_dt,'%Y%m%d') = :basicYmd and auth_status = 'SIGNUP' group by cust_id) lst union " +
            "select (case when lst.auth_status = 'SIGNUPREG' then 'regCnt' " +
            "when lst.auth_status = 'SIGNUP' or lst.auth_status = 'SIGNOUTREG' then 'asctCnt' " +
            "when lst.auth_status = 'SIGNOUT' then 'outCnt' " +
            "else '' end) as col, COUNT(*) as cnt " +
            "from (" +
            "select ROW_NUMBER() OVER(partition by cust_id order by login_dt desc) as rn, lh.* " +
            "from login_hst lh " +
            "where auth_status not in ('LOGIN','LOGOUT') " +
            "and DATE_FORMAT(login_dt,'%Y%m%d') = :basicYmd) lst " +
            "where lst.rn = 1 " +
            "group by lst.auth_status", nativeQuery = true)
    List<Map<String, String>> getCustStatTarget(String basicYmd);

    /**
     * 현재 고객 등급 현황 조회
     * @return
     */
    @Query(value="select 'regAccCnt' as col, COUNT(*) as cnt from cust c1 where cust_status != '02' and cust_grade = '02' union " +
            "select 'asctAccCnt' as col, COUNT(*) as cnt from cust c2 where cust_status != '02' and cust_grade = '01' union " +
            "select 'outAccCnt' as col, COUNT(*) as cnt from cust c3 where cust_status = '02'", nativeQuery = true)
    List<Map<String, String>> getCustStatAccTarget();
}
