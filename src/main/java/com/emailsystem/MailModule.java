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
    
    public static void main(String[] args) {
        EmailBean bean = new EmailBean();
        bean.setFrom("send.1633867@gmail.com");
        bean.setTo(new String[] {"receive.1633867@gmail.com"});
        bean.setSubject("Hello from the other side");
        bean.setTextMsg("<h1><u>Test:</u> Email thinggy <p> Im sending this using programming and <h3>yes</h3> i am just that cool. </p>");
        //bean.setCc(new String[] {"cc1@gmail.com", "cc2@gmail.com"});
//        bean.setHTMLMsg("<html><META http-equiv=Content-Type "
//                            + "content=\"text/html; charset=utf-8\">"
//                            + "<body><h1>Here is my photograph embedded in "
//                            + "this email.</h1>"
//                            + "<h2>I'm flying!</h2></body></html>");
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        List<AttachmentBean> embed = new ArrayList<AttachmentBean>();
        try {
            embed.add(new AttachmentBean("thumbsup.jpg", Files.readAllBytes(new File("thumbsup.jpg").toPath())));
            embed.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            attach.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            bean.setAttach(attach);
            bean.setEmbedAttach(embed);
        } catch (Exception e) {
            
        }
        MailModule mail = new MailModule();
        mail.send(bean);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOG.error("Threaded sleep failed", e);
            System.exit(1);
        }
        mail.receive();
    }
    
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
                        System.out.println("\n\n===[" + email.messageNumber() + "]===");

                        // common info
                        System.out.println("FROM:" + email.from());
                        beans[index].setFrom(email.from().toString());
                        
                        // Must have at least one to address
                        System.out.print("TO:");
                        System.out.println(Arrays.toString(email.to()));
                        beans[index].setTo(emailArrayToStringArray(email.to()));
                        if (email.cc().length != 0) {
                            System.out.print("CC:");
                            System.out.println(Arrays.toString(email.cc()));
                            beans[index].setCc(emailArrayToStringArray(email.cc()));
                        }
                        System.out.println("SUBJECT:" + email.subject());
                        beans[index].setSubject(email.subject());
                        System.out.println("PRIORITY:" + email.priority());
                        beans[index].setPriority(email.priority());
                        System.out.println("SENT DATE:" + email.sentDate());
                        beans[index].setSentTime(LocalDateTime.ofInstant(email.sentDate().toInstant(), ZoneId.systemDefault()));
                        System.out.println("RECEIVED DATE: " + email.receivedDate());
                        beans[index].setReceivedTime(LocalDateTime.ofInstant(email.receivedDate().toInstant(), ZoneId.systemDefault()));
                        
                        
                        // process messages
                        List<EmailMessage> messages = email.messages();
                        System.out.println(messages.size());
                        for (EmailMessage msg: messages) {
                            LOG.info("------");
                            if (msg.getMimeType().equalsIgnoreCase("Text/Plain")) {
                                System.out.println("============================================ PLAIN =============================================================");
                                beans[index].setTextMsg(msg.getContent());
                            } else if (msg.getMimeType().equalsIgnoreCase("Text/Html")) {
                                System.out.println("============================================ HTML =============================================================");
                                beans[index].setHTMLMsg(msg.getContent());
                            }
                            LOG.info("Encoding: " + msg.getEncoding());
                        }

                        // process attachments
                        List<EmailAttachment<? extends DataSource>> attachments = email.attachments();
                        if (attachments != null) {
                            List<AttachmentBean> embed = new ArrayList<>();
                            List<AttachmentBean> normalAttach = new ArrayList<>();
                            for (EmailAttachment attach: attachments) {
                                if (attach.isEmbedded()) {
                                    embed.add(new AttachmentBean(attach.getName(), attach.toByteArray()));
                                } else {
                                    normalAttach.add(new AttachmentBean(attach.getName(), attach.toByteArray()));
                                }
                            }
                            beans[index].setAttach(normalAttach);
                            beans[index].setEmbedAttach(embed);
                        }
                        index++;
                    }
                    return beans;
                } else {
                    System.out.println("No emails on IMAP server");
                } 
            }
            
        } else {
            LOG.info("Unable to send email because either send or recieve addresses are invalid");
        }  
        return null;
    }
    
    private String[] emailArrayToStringArray(EmailAddress[] emails) {
        String[] stringEmails = new String[emails.length];
        for (int i = 0; i < emails.length; i++) {
            stringEmails[i] = emails[i].toString();
        }
        return stringEmails;
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
