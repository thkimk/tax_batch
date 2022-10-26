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
        return mydataIncome.custId.eq(custId);
    }
    private BooleanExpression orgCodeEq(String orgCode) {
        return mydataIncome.orgCode.eq(orgCode);
    }
    private BooleanExpression accountNumEq(String accountNum) {
        return mydataIncome.accountNum.eq(accountNum);
    }
    private BooleanExpression seqNoEq(String seqNo) {
        return mydataIncome.seqNo.eq(seqNo);
    }
    private BooleanExpression currencyCodeEq(String currencyCode) {
        return mydataIncome.currencyCode.eq(currencyCode);
    }
    private BooleanExpression transDtimeEq(String transDtime) {
        return mydataIncome.transDtime.eq(transDtime);
    }
    private BooleanExpression transNoEq(String transNo) {
        return mydataIncome.transNo.eq(transNo);
    }
    private BooleanExpression transTypeEq(String transType) {
        return mydataIncome.transType.eq(transType);
    }
    private BooleanExpression transClassEq(String transClass) {
        return mydataIncome.transClass.eq(transClass);
    }
    private BooleanExpression transAmtEq(long transAmt) {
        return mydataIncome.transAmt.eq(transAmt);
    }
    private BooleanExpression balanceAmtEq(long balanceAmt) {
        return mydataIncome.balanceAmt.eq(balanceAmt);
    }
    private BooleanExpression seqEq(Integer seq) {
        return mydataIncome.seq.eq(seq);
    }
}
