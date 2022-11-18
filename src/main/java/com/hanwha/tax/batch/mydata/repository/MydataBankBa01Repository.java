package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataBankBa01;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface MydataBankBa01Repository extends JpaRepository<MydataBankBa01, Long> {
    /**
     * 은행(원본) 정보 삭제
     * @param ymd
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba01 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA01(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba02 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA02(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba03 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA03(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba04 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA04(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba11 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA11(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba12 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA12(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba13 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA13(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba21 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA21(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba22 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA22(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba23 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteBA23(String ymd);
}
