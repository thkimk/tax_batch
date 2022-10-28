package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataOutgoing;

import java.util.List;

public interface MydataOutgoingCustomRepository {
    /**
     * 마이데이터 경비(카드) 중복 체크
     * @param mydataOutgoing
     * @return
     */
    List<MydataOutgoing> findByDataPk(MydataOutgoing mydataOutgoing);
}
