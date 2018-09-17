
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
 * SmtpModule which is responsible for configuring the SMTP server and sending
 * EmailBeans into it.
 *
 * @author Itai Bolyasni
 * @version 1.0.0
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

    /**
     * Method that accepts an email bean as a param and sends it to google's
     * SMTP server.
     *
     * @params EmailBean bean - the bean configured by the user
     * @version 1.0.0
     */
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
        for (AttachmentBean attach : bean.getAttach()) {
            email.attachment(EmailAttachment.with().name(attach.getName()).content(attach.getAttach()));
        }
        for (AttachmentBean attach : bean.getEmbedAttach()) {
            email.embeddedAttachment(EmailAttachment.with().name(attach.getName()).content(attach.getAttach()));
            htmlMsgBuilder += "<br><img style='width: 300px; height: 250px' src='cid:" + attach.getName() + "'>";
        }
        htmlMsgBuilder += "</body></html>";
        email.htmlMessage(htmlMsgBuilder);

        createSession(email);

    }

    /* 
        Private method that sends the email to the SMTP server
        @param Email email - the email that the user configured.
     */
    private void createSession(Email email) {
        try (SendMailSession mailSession = smtpServer.createSession()) {
            mailSession.open();
            mailSession.sendMail(email);
            LOG.info("Mail sent");
            mailSession.close();
        }
    }
}
