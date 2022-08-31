package com.hanwha.tax.batch.book.repository;

import com.hanwha.tax.batch.entity.BookOutgoing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookOutgoingRepository extends JpaRepository<BookOutgoing, Long> {
    /**
     * 고객번호 기준으로 경비정보 삭제
     * @param custId
     * @return
     */
    @Transactional
    @Modifying
    @Query(value="delete from book_outgoing bo where bo.cust_id=:custId", nativeQuery = true)
    int deleteByCustId(String custId);
}
