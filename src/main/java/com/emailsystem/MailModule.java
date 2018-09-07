/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.emailsystem;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.ImapServer;
import jodd.mail.MailServer;
import jodd.mail.RFC2822AddressParser;
import jodd.mail.ReceiveMailSession;
import jodd.mail.ReceivedEmail;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;

/**
 *
 * @author 1633867
 */
public class MailModule {
    
    private final String password = "15241524";
    private final static Logger LOG = LoggerFactory.getLogger(MailModule.class);
    
    public static void main(String[] args) {
        EmailBean bean = new EmailBean();
        bean.setFrom("send.1633867@gmail.com");
        bean.setTo(new String[] {"receive.1633867@gmail.com"});
        bean.setSubject("Jodd Test34");
        bean.setTextMsg("<h1>Did it work?</h1>");
//        bean.setHTMLMsg("<html><META http-equiv=Content-Type "
//                            + "content=\"text/html; charset=utf-8\">"
//                            + "<body><h1>Here is my photograph embedded in "
//                            + "this email.</h1>"
//                            + "<h2>I'm flying!</h2></body></html>");
        MailModule mail = new MailModule();
        mail.send(bean);

    }
    
    public void send(EmailBean bean) {
        if (verifyBeanData(bean)) {
            sendPlainEmail(bean);
        }

                
    }
    
    public void receive() {
        
    }
    

    
    private void sendPlainEmail(EmailBean bean) {
            SmtpModule smtpServer = new SmtpModule(bean.getFrom(), password);
            Email email = Email.create()
                    .from(bean.getFrom())
                    .to(bean.getTo())
                    .subject(bean.getSubject())
                    .cc(bean.getCc())
                    .bcc(bean.getBcc())
                    .textMessage(bean.getTextMsg() + " " + LocalDateTime.now())
                    .htmlMessage("<!DOCTYPE HTML><html><head></head><body>" + bean.getTextMsg() + "<br></body></html>")
                    .priority(bean.getPriority())
                    .sentDate(Date.from(bean.getSentTime().atZone(ZoneId.systemDefault()).toInstant()));
            for(AttachmentBean attach: bean.getAttach()) {
                email.attachment(EmailAttachment.with().content(attach.getAttach()));
            }
            for (AttachmentBean attach: bean.getEmbedAttach()) {
                email.embeddedAttachment(EmailAttachment.with().content(attach.getAttach()));
            }
            
            try ( SendMailSession mailSession = smtpServer.createSession() ) {
                mailSession.open();
                mailSession.sendMail(email);
                LOG.info("Mail sent");
                mailSession.close();
            }
    }
    
    private boolean verifyBeanData(EmailBean bean) {

        if (!checkEmail(bean.getFrom())) {
            LOG.error("Email: " + bean.getFrom() + " is invalid!");
            return false;
        }
        else if (!checkEmail(bean.getTo())) {
            LOG.error("Email is invalid! 1");
            return false;
        }
        else if (!checkEmail(bean.getCc())) {
            LOG.error("Email is invalid! 2");
            return false;
        }
        else if (!checkEmail(bean.getBcc())) {
            LOG.error("Email is invalid! 3");
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
