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
                .where(custIdEq(mi.getCustId()), orgCodeEq(mi.getOrgCode()), cardIdEq(mi.getCardId()), apprDtimeEq(mi.getApprDtime()), apprNumEq(mi.getApprNum()), statusEq(mi.getStatus()), transDtimeEq(mi.getTransDtime()), payTypeEq(mi.getPayType()), apprAmtEq(mi.getApprAmt()), seqEq(mi.getSeq()))
                .fetch();
    }

    private BooleanExpression custIdEq(String custId) {
        return mydataOutgoing.custId.eq(custId);
    }
    private BooleanExpression orgCodeEq(String orgCode) {
        return mydataOutgoing.orgCode.eq(orgCode);
    }
    private BooleanExpression cardIdEq(String cardId) {
        return mydataOutgoing.cardId.eq(cardId);
    }
    private BooleanExpression apprDtimeEq(String apprDtime) {
        return mydataOutgoing.apprDtime.eq(apprDtime);
    }
    private BooleanExpression apprNumEq(String apprNum) {
        return mydataOutgoing.apprNum.eq(apprNum);
    }
    private BooleanExpression statusEq(String status) {
        return mydataOutgoing.status.eq(status);
    }
    private BooleanExpression transDtimeEq(String transDtime) {
        return mydataOutgoing.transDtime.eq(transDtime);
    }
    private BooleanExpression payTypeEq(String payType) {
        return mydataOutgoing.payType.eq(payType);
    }
    private BooleanExpression apprAmtEq(long apprAmt) {
        return mydataOutgoing.apprAmt.eq(apprAmt);
    }
    private BooleanExpression seqEq(int seq) {
        return mydataOutgoing.seq.eq(seq);
    }
}
