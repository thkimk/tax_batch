package com.hanwha.tax.batch.fingerauto.mail.service;

import com.hanwha.tax.batch.fingerauto.entity.MailHistory;
import com.hanwha.tax.batch.fingerauto.entity.MailReceiver;
import com.hanwha.tax.batch.fingerauto.mail.repository.MailHistoryRepository;
import com.hanwha.tax.batch.fingerauto.mail.repository.MailReceiverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("mailService")
public class MailService {

    @Autowired
    private MailHistoryRepository mailHistoryRepository;

    @Autowired
    private MailReceiverRepository mailReceiverRepository;

    public void sendMail(String subject, String content, String email, String name) {
        MailHistory mailHistory = new MailHistory(subject, content);
        mailHistory = mailHistoryRepository.save(mailHistory);
        MailReceiver mailReceiver = new MailReceiver(mailHistory.getMailNo(), name, email, "");
        mailReceiverRepository.save(mailReceiver);
    }
}
