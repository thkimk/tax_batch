package com.hanwha.tax.batch.fingerauto.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity(name = "mail_history")
public class MailHistory {

    @Id
    private Long mailNo;

    private Long mailRevNo;

    private Long mailTmptSeq;

    private Long mailSerialnum;

    private Long mailSendFlag;

    private Long mailSendOrder;

    private Long mailSendReserve;

    private Long mailSendCount;

    private String mailSendSubject;

    private String mailSendContents;

    private Long mailSendResendYn;

    private String mailSendUserid;

    private String mailSendName;

    private String mailSendEmail;

    private Long mailSendSuccessCount;

    private Long mailSendFailedCount;

    private Long mailSendDenyCount;

    private Long mailSendDate;

    private Long mailSendCompleteDate;


    public MailHistory(String subject, String content) {
        Long unixTime = System.currentTimeMillis();

        mailNo = unixTime;
        mailSendSubject = subject;
        mailSendContents = content;
        mailSendName = "소크라택스";
        mailSendEmail = "webmaster@socratax.io";
        {
            mailRevNo = 0L;
            mailTmptSeq = 0L;
            mailSerialnum = 0L;
            mailSendFlag = 1L;
            mailSendOrder = 1L;
            mailSendReserve = 0L;
            mailSendCount = 1L;

            mailSendSuccessCount = 0L;
            mailSendFailedCount = 0L;
            mailSendDenyCount = 0L;
            mailSendDate = unixTime/1000 + 60;  // 60초 후 발송
            mailSendCompleteDate = 0L;

            mailSendResendYn = 0L;
            mailSendUserid = "";
        }
    }

}