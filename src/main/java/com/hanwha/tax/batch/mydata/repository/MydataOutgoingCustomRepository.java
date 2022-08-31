package com.hanwha.tax.batch.mydata.repository;

import com.hanwha.tax.batch.entity.MydataOutgoing;

import java.util.List;

public interface MydataOutgoingCustomRepository {
    List<MydataOutgoing> findByDataPk(MydataOutgoing mydataOutgoing);
}
