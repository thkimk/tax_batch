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
    BookIncomeRepository bookIncomeRepository;

    @Autowired
    BookOutgoingRepository bookOutgoingRepository;

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
}
