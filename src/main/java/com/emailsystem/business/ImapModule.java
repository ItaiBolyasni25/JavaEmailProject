/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.business;

import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataSource;
import javax.mail.Flags;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailFilter;
import jodd.mail.EmailMessage;
import jodd.mail.ImapServer;
import jodd.mail.MailServer;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633867
 */
public class ImapModule {

    private final String imapServerName = "imap.gmail.com";
    ImapServer imapServer;
    private final static Logger LOG = LoggerFactory.getLogger(MailModule.class);

    public ImapModule(String receiveEmail, String password) {
        imapServer = MailServer.create()
                .host(imapServerName)
                .ssl(true)
                .auth(receiveEmail, password)
                .debugMode(true)
                .buildImapMailServer();
    }
    
    public EmailBean[] receive() {
        int index = 0;
        EmailBean[] beans = null;

        try (ReceiveMailSession session = imapServer.createSession()) {
            session.open();
            ReceivedEmail[] emails = session.receiveEmailAndMarkSeen((EmailFilter.filter().flag(Flags.Flag.SEEN, false)));
            if (emails != null) {
                beans = new EmailBean[emails.length];
                for (ReceivedEmail email : emails) {
                    beans[index] = new EmailBean();

                    // set the bean
                    beans[index].setFrom(email.from().toString());
                    beans[index].setTo(emailArrayToStringArray(email.to()));
                    beans[index].setCc(emailArrayToStringArray(email.cc()));
                    beans[index].setSubject(email.subject());
                    beans[index].setPriority(email.priority());
                    beans[index].setSentTime(LocalDateTime.ofInstant(email.sentDate().toInstant(), ZoneId.systemDefault()));
                    beans[index].setReceivedTime(LocalDateTime.ofInstant(email.receivedDate().toInstant(), ZoneId.systemDefault()));

                    // process messages
                    beans[index].setTextMsg(processMessages(email.messages(), false));
                    beans[index].setHTMLMsg(processMessages(email.messages(), true));

                    // process attachments
                    beans[index].setAttach(processAttachments(email.attachments(), false));
                    beans[index].setEmbedAttach(processAttachments(email.attachments(), true));
                }
                index++;
            }
            return beans;
        }
    }

    private String processMessages(List<EmailMessage> messages, boolean isHtml) {
        for (EmailMessage msg : messages) {
            if (msg.getMimeType().equalsIgnoreCase("Text/Plain") && !isHtml) {
                return msg.getContent();
            } else if (msg.getMimeType().equalsIgnoreCase("Text/Html") && isHtml) {
                return msg.getContent();
            }
        }
        return "";
    }

    private List<AttachmentBean> processAttachments(List<EmailAttachment<? extends DataSource>> attachList, boolean embed) {
        List<AttachmentBean> beanList = new ArrayList<>();
        for (EmailAttachment attach : attachList) {
            if (attachList != null) {
                if (embed && attach.isEmbedded()) {
                    // get all embed files
                    beanList.add(new AttachmentBean(attach.getName(), attach.toByteArray()));
                } else if (!embed && !attach.isEmbedded()) {
                    // get all attached filess
                    beanList.add(new AttachmentBean(attach.getName(), attach.toByteArray()));
                }
            }
        }
        return beanList;
    }

    private String[] emailArrayToStringArray(EmailAddress[] emails) {
        String[] stringEmails = new String[emails.length];
        for (int i = 0; i < emails.length; i++) {
            stringEmails[i] = emails[i].toString();
        }
        return stringEmails;
    }
}
