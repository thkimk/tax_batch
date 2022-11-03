package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataCardCd01;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MydataCardCd01Repository extends JpaRepository<MydataCardCd01, Long> {
    /**
     * 카드(원본) 테이블 초기화
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd01", nativeQuery = true)
    int truncateCD01();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd03", nativeQuery = true)
    int truncateCD03();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd04", nativeQuery = true)
    int truncateCD04();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd11", nativeQuery = true)
    int truncateCD11();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd21", nativeQuery = true)
    int truncateCD21();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd22", nativeQuery = true)
    int truncateCD22();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd23", nativeQuery = true)
    int truncateCD23();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd24", nativeQuery = true)
    int truncateCD24();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd31", nativeQuery = true)
    int truncateCD31();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd32", nativeQuery = true)
    int truncateCD32();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_card_cd33", nativeQuery = true)
    int truncateCD33();
}
