package com.hanwha.tax.batch.fingerauto.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity(name = "mail_receiver")
public class MailReceiver {

    @Id
    private Long mailReceiverNo;

    private Long mailReceiverRevNo;

    private Long mailNo;

    private Long mailTmptSeq;

    private Long mailSerialnum;

    private Long mailReceiverReportSeq;

    private String mailReceiverName;

    private String mailReceiverEmail;

    private String mailReceiverReplaceJson;

    private Long mailReceiverOpen;

    private Long mailReceiverOpenDate;

    private Long mailReceiverLinkOpen;

    private Long mailReceiverLinkOpenDate;

    private String mailReceiverSmtpMsg;

    private Long mailReceiverResultCode;

    private Long mailReceiverResend;

    private Long mailReceiverReportResp;

    private Long mailReceiverSendDate;

    private Long mailReceiverCompleteDate;

    private String mailSenderIp;

    public MailReceiver(Long mailNo, String name, String mailAddr, String sendIp) {
        Long unixTime = System.currentTimeMillis();

        this.mailNo = mailNo;
        mailReceiverNo = unixTime;
        mailReceiverName = name;
        mailReceiverEmail = mailAddr;
        mailReceiverSendDate = unixTime/1000 + 60;  // 60초 후 발송
        mailSenderIp = sendIp;

        {
            mailReceiverRevNo = 0L;
            mailTmptSeq = 0L;
            mailSerialnum = 0L;
            mailReceiverReportSeq = 0L;

            mailReceiverReplaceJson = "";
            mailReceiverOpen = 0L;
            mailReceiverOpenDate = 0L;
            mailReceiverLinkOpen = 0L;
            mailReceiverLinkOpenDate = 0L;

            mailReceiverCompleteDate = 0L;

            mailReceiverSmtpMsg = "";
            mailReceiverResultCode = 1L;
            mailReceiverResend = 1L;
            mailReceiverReportResp = 0L;
        }
    }


}