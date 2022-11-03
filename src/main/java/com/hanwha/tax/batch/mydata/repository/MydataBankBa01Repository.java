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
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba01", nativeQuery = true)
    int deleteBA01();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba02", nativeQuery = true)
    int deleteBA02();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba03", nativeQuery = true)
    int deleteBA03();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba04", nativeQuery = true)
    int deleteBA04();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba11", nativeQuery = true)
    int deleteBA11();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba12", nativeQuery = true)
    int deleteBA12();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba13", nativeQuery = true)
    int deleteBA13();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba21", nativeQuery = true)
    int deleteBA21();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba22", nativeQuery = true)
    int deleteBA22();
    @Transactional
    @Modifying
    @Query(value="delete from mydata_bank_ba23", nativeQuery = true)
    int deleteBA23();

    /**
     * 은행(원본) 시퀀스 초기화
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba01 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA01();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba02 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA02();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba03 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA03();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba04 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA04();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba11 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA11();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba12 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA12();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba13 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA13();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba21 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA21();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba22 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA22();
    @Transactional
    @Modifying
    @Query(value="alter table mydata_bank_ba23 auto_increment = 1", nativeQuery = true)
    void resetSequenceBA23();
}
