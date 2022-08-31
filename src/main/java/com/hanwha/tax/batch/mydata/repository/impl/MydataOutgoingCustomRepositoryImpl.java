package com.hanwha.tax.batch.mydata.repository.impl;

import com.hanwha.tax.batch.Utils;
import com.hanwha.tax.batch.entity.MydataOutgoing;
import com.hanwha.tax.batch.mydata.repository.MydataOutgoingCustomRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hanwha.tax.batch.entity.QMydataOutgoing.mydataOutgoing;

@Repository
public class MydataOutgoingCustomRepositoryImpl implements MydataOutgoingCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public MydataOutgoingCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public List<MydataOutgoing> findByDataPk(MydataOutgoing mi) {
        return jpaQueryFactory.selectFrom(mydataOutgoing)
                .where(custIdEq(mi.getCustId()), orgCodeEq(mi.getOrgCode()), cardIdEq(mi.getCustId()), apprDtimeEq(mi.getApprDtime()), apprNumEq(mi.getApprNum()), statusEq(mi.getStatus()), transDtimeEq(mi.getTransDtime()), payTypeEq(mi.getPayType()), apprAmtEq(mi.getApprAmt()), seqEq(mi.getSeq()))
                .fetch();
    }

    private BooleanExpression custIdEq(String custId) {
        return !Utils.isEmpty(custId) ? mydataOutgoing.custId.eq(custId) : null;
    }
    private BooleanExpression orgCodeEq(String orgCode) {
        return !Utils.isEmpty(orgCode) ? mydataOutgoing.orgCode.eq(orgCode) : null;
    }
    private BooleanExpression cardIdEq(String cardId) {
        return !Utils.isEmpty(cardId) ? mydataOutgoing.cardId.eq(cardId) : null;
    }
    private BooleanExpression apprDtimeEq(String apprDtime) {
        return !Utils.isEmpty(apprDtime) ? mydataOutgoing.apprDtime.eq(apprDtime) : null;
    }
    private BooleanExpression apprNumEq(String apprNum) {
        return !Utils.isEmpty(apprNum) ? mydataOutgoing.apprNum.eq(apprNum) : null;
    }
    private BooleanExpression statusEq(String status) {
        return !Utils.isEmpty(status) ? mydataOutgoing.status.eq(status) : null;
    }
    private BooleanExpression transDtimeEq(String transDtime) {
        return !Utils.isEmpty(transDtime) ? mydataOutgoing.transDtime.eq(transDtime) : null;
    }
    private BooleanExpression payTypeEq(String payType) {
        return !Utils.isEmpty(payType) ? mydataOutgoing.payType.eq(payType) : null;
    }
    private BooleanExpression apprAmtEq(long apprAmt) {
        return 0 < apprAmt ? mydataOutgoing.apprAmt.eq(apprAmt) : null;
    }
    private BooleanExpression seqEq(int seq) {
        return 0 < seq ? mydataOutgoing.seq.eq(seq) : null;
    }
}
