/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.emailsystem.business;

import com.emailsystem.data.EmailBean;
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
        if (verifyBeanData(bean) && bean != null) {
            SmtpModule smtp = new SmtpModule(bean.getFrom(), password);
            smtp.sendEmail(bean);
        }         
    }
    
    public EmailBean[] receive() {
        EmailBean[] emails = null;
        if (checkEmail(receiveEmail)) {
            ImapModule imap = new ImapModule(receiveEmail, password);
            emails = imap.receive();
        }
        return emails;
    }
    
    private boolean verifyBeanData(EmailBean bean) {

        if (bean.getFrom() == null || !checkEmail(bean.getFrom())) {
            LOG.error("Email: " + bean.getFrom() + " is invalid!");
            return false;
        }
        else if (bean.getTo() == null || !checkEmail(bean.getTo())) {
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
