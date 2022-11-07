package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataCardCd01;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MydataCardCd01Repository extends JpaRepository<MydataCardCd01, Long> {
    /**
     * 카드(원본) 정보 삭제
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd01", nativeQuery = true)
    int deleteCD01();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd03", nativeQuery = true)
    int deleteCD03();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd04", nativeQuery = true)
    int deleteCD04();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd11", nativeQuery = true)
    int deleteCD11();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd21", nativeQuery = true)
    int deleteCD21();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd22", nativeQuery = true)
    int deleteCD22();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd23", nativeQuery = true)
    int deleteCD23();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd24", nativeQuery = true)
    int deleteCD24();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd31", nativeQuery = true)
    int deleteCD31();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd32", nativeQuery = true)
    int deleteCD32();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_card_cd33", nativeQuery = true)
    int deleteCD33();
}
