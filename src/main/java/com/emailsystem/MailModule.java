/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.emailsystem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.activation.DataSource;
import javax.mail.Flags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.mail.Email;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailFilter;
import jodd.mail.EmailMessage;
import jodd.mail.ImapServer;
import jodd.mail.MailServer;
import jodd.mail.RFC2822AddressParser;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author 1633867
 */
public class MailModule {
    
    private final String password = "15241524";
    private final static Logger LOG = LoggerFactory.getLogger(MailModule.class);
    private final String receiveEmail = "receive.1633867@gmail.com";
    
    public void send(EmailBean bean)  {
        if (verifyBeanData(bean)) {
            sendPlainEmail(bean);
        }         
    }
    
    public EmailBean[] receive() {
        if (checkEmail(receiveEmail)) {
            int index = 0;
            ImapModule imapServer = new ImapModule(receiveEmail, password);
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
                        List<EmailMessage> messages = email.messages();
                        System.out.println(messages.size());
                        for (EmailMessage msg: messages) {
                            LOG.info("------");
                            if (msg.getMimeType().equalsIgnoreCase("Text/Plain")) {
                                beans[index].setTextMsg(msg.getContent());
                            } else if (msg.getMimeType().equalsIgnoreCase("Text/Html")) {
                                beans[index].setHTMLMsg(msg.getContent());
                            }
                        }
                        // process attachments
                            beans[index].setAttach(processAttachments(email.attachments(), false));
                            beans[index].setEmbedAttach(processAttachments(email.attachments(), true));
                        }
                        index++;
                    }
                    return beans;
                } 
            } else {
            LOG.info("Unable to send email because either send or recieve addresses are invalid");
        }  
        return null;
    }
    
    private String processMessages(EmailMessage msg) {
        
    }


    
    private List<AttachmentBean> processAttachments(List<EmailAttachment <?extends DataSource>> attachList, boolean embed) {
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
    
    
    private void sendPlainEmail(EmailBean bean) {
            SmtpModule smtpServer = new SmtpModule(bean.getFrom(), password);
            Email email = Email.create()
                    .from(bean.getFrom())
                    .to(bean.getTo())
                    .subject(bean.getSubject())
                    .cc(bean.getCc())
                    .bcc(bean.getBcc())
                    .textMessage(bean.getTextMsg())
                    .priority(bean.getPriority())
                    .sentDate(Date.from(bean.getSentTime().atZone(ZoneId.systemDefault()).toInstant()));
            String htmlMsgBuilder = "<!DOCTYPE HTML><html><head></head><body>" + bean.getTextMsg();
            for(AttachmentBean attach: bean.getAttach()) {
                email.attachment(EmailAttachment.with().name(attach.getName()).content(attach.getAttach()));
            }
            for (AttachmentBean attach: bean.getEmbedAttach()) {
                email.embeddedAttachment(EmailAttachment.with().name(attach.getName()).content(attach.getAttach()));
                htmlMsgBuilder += "<br><h2>This is attached </h2><img style='width: 300px; height: 250px' src='cid:"+ attach.getName() + "'>";
            }
            htmlMsgBuilder += "</body></html>";
            email.htmlMessage(htmlMsgBuilder);
            
            try ( SendMailSession mailSession = smtpServer.createSession() ) {
                mailSession.open();
                mailSession.sendMail(email);
                LOG.info("Mail sent");
                mailSession.close();
            }
    }
    
    private String[] emailArrayToStringArray(EmailAddress[] emails) {
        String[] stringEmails = new String[emails.length];
        for (int i = 0; i < emails.length; i++) {
            stringEmails[i] = emails[i].toString();
        }
        return stringEmails;
    }

    
    private boolean verifyBeanData(EmailBean bean) {

        if (!checkEmail(bean.getFrom())) {
            LOG.error("Email: " + bean.getFrom() + " is invalid!");
            return false;
        }
        else if (!checkEmail(bean.getTo())) {
            LOG.error("Email is invalid!");
            return false;
        }
        else if (!checkEmail(bean.getCc())) {
            LOG.error("Email is invalid!");
            return false;
        }
        else if (!checkEmail(bean.getBcc())) {
            LOG.error("Email is invalid!");
            return false;
        }
        return true;
    }
    
        private boolean checkEmail(String... emails) {
        for(String email : emails) {
            if (RFC2822AddressParser.STRICT.parseToEmailAddress(email) == null) {
                System.err.println("Email: " + email + "is invalid!");
                return false;
            }
        }
        return true;
    }
}
