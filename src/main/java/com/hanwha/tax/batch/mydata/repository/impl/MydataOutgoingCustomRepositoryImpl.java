package com.hanwha.tax.batch.mydata.repository.impl;

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
        return custId != null ? mydataOutgoing.custId.eq(custId) : mydataOutgoing.custId.isNull();
    }
    private BooleanExpression orgCodeEq(String orgCode) {
        return orgCode != null ? mydataOutgoing.orgCode.eq(orgCode) : mydataOutgoing.orgCode.isNull();
    }
    private BooleanExpression cardIdEq(String cardId) {
        return cardId != null ? mydataOutgoing.cardId.eq(cardId) : mydataOutgoing.cardId.isNull();
    }
    private BooleanExpression apprDtimeEq(String apprDtime) {
        return apprDtime != null ? mydataOutgoing.apprDtime.eq(apprDtime) : mydataOutgoing.apprDtime.isNull();
    }
    private BooleanExpression apprNumEq(String apprNum) {
        return apprNum != null ? mydataOutgoing.apprNum.eq(apprNum) : mydataOutgoing.apprNum.isNull();
    }
    private BooleanExpression statusEq(String status) {
        return status != null ? mydataOutgoing.status.eq(status) : mydataOutgoing.status.isNull();
    }
    private BooleanExpression transDtimeEq(String transDtime) {
        return transDtime != null ? mydataOutgoing.transDtime.eq(transDtime) : mydataOutgoing.transDtime.isNull();
    }
    private BooleanExpression payTypeEq(String payType) {
        return payType != null ? mydataOutgoing.payType.eq(payType) : mydataOutgoing.payType.isNull();
    }
    private BooleanExpression apprAmtEq(long apprAmt) {
        return mydataOutgoing.apprAmt.eq(apprAmt);
    }
    private BooleanExpression seqEq(Integer seq) {
        return seq != null ? mydataOutgoing.seq.eq(seq) : null;
    }
}
