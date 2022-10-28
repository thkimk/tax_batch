package com.hanwha.tax.batch.mydata.repository.impl;

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
        return custId != null ? mydataIncome.custId.eq(custId) : mydataIncome.custId.isNull();
    }
    private BooleanExpression orgCodeEq(String orgCode) {
        return orgCode != null ? mydataIncome.orgCode.eq(orgCode) : mydataIncome.orgCode.isNull();
    }
    private BooleanExpression accountNumEq(String accountNum) {
        return accountNum != null ? mydataIncome.accountNum.eq(accountNum) : mydataIncome.accountNum.isNull();
    }
    private BooleanExpression seqNoEq(String seqNo) {
        return seqNo != null ? mydataIncome.seqNo.eq(seqNo) : mydataIncome.seqNo.isNull();
    }
    private BooleanExpression currencyCodeEq(String currencyCode) {
        return currencyCode != null ? mydataIncome.currencyCode.eq(currencyCode) : mydataIncome.currencyCode.isNull();
    }
    private BooleanExpression transDtimeEq(String transDtime) {
        return transDtime != null ? mydataIncome.transDtime.eq(transDtime) : mydataIncome.transDtime.isNull();
    }
    private BooleanExpression transNoEq(String transNo) {
        return transNo != null ? mydataIncome.transNo.eq(transNo) : mydataIncome.transNo.isNull();
    }
    private BooleanExpression transTypeEq(String transType) {
        return transType != null ? mydataIncome.transType.eq(transType) : mydataIncome.transType.isNull();
    }
    private BooleanExpression transClassEq(String transClass) {
        return transClass != null ? mydataIncome.transClass.eq(transClass) : mydataIncome.transClass.isNull();
    }
    private BooleanExpression transAmtEq(long transAmt) {
        return mydataIncome.transAmt.eq(transAmt);
    }
    private BooleanExpression balanceAmtEq(long balanceAmt) {
        return mydataIncome.balanceAmt.eq(balanceAmt);
    }
    private BooleanExpression seqEq(Integer seq) {
        return seq != null ? mydataIncome.seq.eq(seq) : mydataIncome.seq.isNull();
    }
}
