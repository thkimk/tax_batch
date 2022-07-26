package com.hanwha.tax.batch.book.service;

import com.hanwha.tax.batch.book.repository.BookIncomeRepository;
import com.hanwha.tax.batch.book.repository.BookOutgoingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("bookService")
public class BookService {

    @Autowired
    private BookIncomeRepository bookIncomeRepository;

    @Autowired
    private BookOutgoingRepository bookOutgoingRepository;

    /**
     * 고객번호로 수입정보 삭제
     * @param custId
     * @return
     */
    public int deleteBookIncomeByCustId(String custId) {
        return bookIncomeRepository.deleteByCustId(custId);
    }

    /**
     * 고객번호로 경비정보 삭제
     * @param custId
     * @return
     */
    public int deleteBookOutgoingByCustId(String custId) {
        return bookOutgoingRepository.deleteByCustId(custId);
    }

    /**
     * 특정 연도 간편장부 지출 금액 조회
     * @param custId
     * @return
     */
    public long getInvalByYear(String custId, int year) {
        return bookOutgoingRepository.getInvalByYear(custId, year);
    }
}
