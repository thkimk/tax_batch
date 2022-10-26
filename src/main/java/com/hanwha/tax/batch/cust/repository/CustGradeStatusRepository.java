package com.hanwha.tax.batch.cust.repository;

import com.hanwha.tax.batch.entity.CustGradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustGradeStatusRepository extends JpaRepository<CustGradeStatus, String> {
    /**
     * 기준일 별 고객 등급 현황 정보 계산
     * @param basicYmd
     * @return
     */
    @Query(value="select 'newCnt' as grade, count(*) as cnt from cust c1 where DATE_FORMAT(create_dt,'%Y%m%d') = :basicYmd union " +
            "select 'regCnt' as grade, COUNT(*) as cnt from cust c2 where cust_status != '02' and cust_grade = '02' and DATE_FORMAT(reg_in_dt,'%Y%m%d') = :basicYmd union " +
            "select 'asctCnt' as grade, COUNT(*) as cnt from cust c3 where cust_status != '02' and cust_grade = '01' and DATE_FORMAT(asct_in_dt,'%Y%m%d') = :basicYmd union " +
            "select 'outCnt' as grade, COUNT(*) as cnt from (" +
            "select c4.* from cust c4 where cust_status = '02' and cust_grade = '02' and DATE_FORMAT(reg_out_dt ,'%Y%m%d') = :basicYmd union " +
            "select c5.* from cust c5 where cust_status = '02' and cust_grade = '01' and DATE_FORMAT(asct_out_dt ,'%Y%m%d') = :basicYmd" +
            ") tmp", nativeQuery = true)
    List<Map<String, String>> getCustGradeStatusTarget(String basicYmd);

    /**
     * 현재 고객 등급 현황 조회
     * @return
     */
    @Query(value="select 'regAccCnt' as grade, COUNT(*) as cnt from cust c1 where cust_status != '02' and cust_grade = '02' union " +
            "select 'asctAccCnt' as grade, COUNT(*) as cnt from cust c2 where cust_status != '02' and cust_grade = '01' union " +
            "select 'outAccCnt' as grade, COUNT(*) as cnt from cust c3 where cust_status = '02'", nativeQuery = true)
    List<Map<String, String>> getCustGradeStatusTarget();
}
