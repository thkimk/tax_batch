package com.hanwha.tax.batch.book.repository;

import com.hanwha.tax.batch.entity.BookIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookIncomeRepository extends JpaRepository<BookIncome, Long> {
    /**
     * 고객번호 기준으로 수입정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from book_income bi where bi.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}
