package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataBankBa01;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface MydataBankBa01Repository extends JpaRepository<MydataBankBa01, Long> {
    /**
     * 은행(원본) 테이블 초기화
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba01", nativeQuery = true)
    int deleteBA01();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba02", nativeQuery = true)
    int truncateBA02();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba03", nativeQuery = true)
    int truncateBA03();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba04", nativeQuery = true)
    int truncateBA04();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba11", nativeQuery = true)
    int truncateBA11();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba12", nativeQuery = true)
    int truncateBA12();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba13", nativeQuery = true)
    int truncateBA13();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba21", nativeQuery = true)
    int truncateBA21();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba22", nativeQuery = true)
    int truncateBA22();
    @Transactional
    @Modifying
    @Query(value="truncate table mydata_bank_ba23", nativeQuery = true)
    int truncateBA23();

    /**
     * 은행(원본) 시퀀스 초기화
     * @return
     */
    @Query(value="alter table mydata_bank_ba01 auto_increment = 1", nativeQuery = true)
    int resetSequenceBA01();
    @Query(value="show table status where name = 'mydata_bank_ba01'", nativeQuery = true)
    Map<String, String> checkSequenceBA01();
}
