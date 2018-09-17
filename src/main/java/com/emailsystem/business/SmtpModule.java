/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.business;

import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import java.time.ZoneId;
import java.util.Date;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.MailServer;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633867
 */
public class SmtpModule {
    
    private SmtpServer smtpServer;
    private final String smtpHost = "smtp.gmail.com";
    private final static Logger LOG = LoggerFactory.getLogger(MailModule.class);

    
    public SmtpModule(String email, String password) {
        this.smtpServer = MailServer.create()
            .ssl(true)
            .host(smtpHost)
            .auth(email, password)
            .debugMode(true)
            .buildSmtpMailServer();
    }
    
    public void sendEmail(EmailBean bean) {
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
            
            createSession(email);

    }
    
    private void createSession(Email email) {
            try ( SendMailSession mailSession = smtpServer.createSession() ) {
                mailSession.open();
                mailSession.sendMail(email);
                LOG.info("Mail sent");
                mailSession.close();
            } 
    }
}
