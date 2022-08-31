package com.hanwha.tax.batch.mydata.repository.impl;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.entity.MydataIncome;
import com.hanwha.tax.batch.mydata.repository.MydataIncomeCustomRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hanwha.tax.batch.entity.QMydataIncome.mydataIncome;

@Repository
public class MydataIncomeCustomRepositoryImpl implements MydataIncomeCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public MydataIncomeCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<MydataIncome> findByDataPk(MydataIncome mi) {
        return jpaQueryFactory.selectFrom(mydataIncome)
                .where(custIdEq(mi.getCustId()), orgCodeEq(mi.getOrgCode()), accountNumEq(mi.getAccountNum()), seqNoEq(mi.getSeqNo()), currencyCodeEq(mi.getCurrencyCode()), transDtimeEq(mi.getTransDtime()), transNoEq(mi.getTransNo()), transTypeEq(mi.getTransType()), transClassEq(mi.getTransClass()), transAmtEq(mi.getTransAmt()), balanceAmtEq(mi.getBalanceAmt()), seqEq(mi.getSeq()))
                .fetch();
    }

    private BooleanExpression custIdEq(String custId) {
        return !Utils.isEmpty(custId) ? mydataIncome.custId.eq(custId) : null;
    }
    private BooleanExpression orgCodeEq(String orgCode) {
        return !Utils.isEmpty(orgCode) ? mydataIncome.orgCode.eq(orgCode) : null;
    }
    private BooleanExpression accountNumEq(String accountNum) {
        return !Utils.isEmpty(accountNum) ? mydataIncome.accountNum.eq(accountNum) : null;
    }
    private BooleanExpression seqNoEq(String seqNo) {
        return !Utils.isEmpty(seqNo) ? mydataIncome.seqNo.eq(seqNo) : null;
    }
    private BooleanExpression currencyCodeEq(String currencyCode) {
        return !Utils.isEmpty(currencyCode) ? mydataIncome.currencyCode.eq(currencyCode) : null;
    }
    private BooleanExpression transDtimeEq(String transDtime) {
        return !Utils.isEmpty(transDtime) ? mydataIncome.transDtime.eq(transDtime) : null;
    }
    private BooleanExpression transNoEq(String transNo) {
        return !Utils.isEmpty(transNo) ? mydataIncome.transNo.eq(transNo) : null;
    }
    private BooleanExpression transTypeEq(String transType) {
        return !Utils.isEmpty(transType) ? mydataIncome.transType.eq(transType) : null;
    }
    private BooleanExpression transClassEq(String transClass) {
        return !Utils.isEmpty(transClass) ? mydataIncome.transClass.eq(transClass) : null;
    }
    private BooleanExpression transAmtEq(long transAmt) {
        return 0 < transAmt ? mydataIncome.transAmt.eq(transAmt) : null;
    }
    private BooleanExpression balanceAmtEq(long balanceAmt) {
        return 0 < balanceAmt ? mydataIncome.balanceAmt.eq(balanceAmt) : null;
    }
    private BooleanExpression seqEq(int seq) {
        return 0 < seq ? mydataIncome.seq.eq(seq) : null;
    }
}
