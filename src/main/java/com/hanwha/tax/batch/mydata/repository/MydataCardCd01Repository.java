package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataCardCd01;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MydataCardCd01Repository extends JpaRepository<MydataCardCd01, Long> {
    /**
     * 카드(원본) 정보 삭제
     * @param ymd
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd01 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD01(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd03 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD03(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd04 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD04(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd11 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD11(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd21 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD21(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd22 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD22(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd23 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD23(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd24 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD24(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd31 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD31(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd32 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD32(String ymd);
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd33 where DATE_FORMAT(create_dt,'%Y%m%d') = :ymd", nativeQuery = true)
    int deleteCD33(String ymd);
}
