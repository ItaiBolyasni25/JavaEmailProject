
package com.emailsystem.business;

import com.emailsystem.data.EmailBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jodd.mail.RFC2822AddressParser;

/**
 * MailModule, the main module that manages the send and receive actions by
 * calling the correct modules.
 *
 * @author Itai Bolyasni
 * @version 1.0.0
 */
public class MailModule {

    private final String password = "15241524";
    private final static Logger LOG = LoggerFactory.getLogger(MailModule.class);
    private final String receiveEmail = "receive.1633867@gmail.com";

    /**
     * Send method, takes an email bean as param and sends it to a specified
     * receiver.
     *
     * @param EmailBean bean
     * @version 1.0.0
     */
    public void send(EmailBean bean) {
        if (verifyBeanData(bean) && bean != null) {
            SmtpModule smtp = new SmtpModule(bean.getFrom(), password);
            smtp.sendEmail(bean);
        }
    }

    /**
     * Receive method, returns a processed emailbean array from the IMAP server
     *
     * @return EmailBean[]
     * @version 1.0.0
     */
    public EmailBean[] receive() {
        EmailBean[] emails = null;
        if (checkEmail(receiveEmail)) {
            ImapModule imap = new ImapModule(receiveEmail, password);
            emails = imap.receive();
        }
        return emails;
    }

    /* 
        Private method that verifies all the bean's information
        @param EmailBean to be checked
     */
    private boolean verifyBeanData(EmailBean bean) {

        if (bean.getFrom() == null || !checkEmail(bean.getFrom())) {
            LOG.error("Email: " + bean.getFrom() + " is invalid!");
            return false;
        } else if (bean.getTo() == null || !checkEmail(bean.getTo())) {
            LOG.error("Receiver email is invalid!");
            return false;
        } else if (!checkEmail(bean.getCc())) {
            LOG.error("Cc email is invalid!");
            return false;
        } else if (!checkEmail(bean.getBcc())) {
            LOG.error("Bcc email is invalid!");
            return false;
        }
        return true;
    }

    /* 
        Private method that verifies email addresses. Can accept one or many Strings
        @param String... emails - one or many emails.
     */
    private boolean checkEmail(String... emails) {
        for (String email : emails) {
            if (RFC2822AddressParser.STRICT.parseToEmailAddress(email) == null) {
                LOG.error("Email: " + email + "is invalid!");
                return false;
            }
        }
        return true;
    }
}
