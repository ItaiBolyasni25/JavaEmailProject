
package com.emailsystem.business;

import com.emailsystem.data.EmailBean;
import java.sql.SQLException;
import java.util.Arrays;
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
public class MailModule implements MailInterface {

    private final String password = "dawsoncollege";
    private final static Logger LOG = LoggerFactory.getLogger(MailModule.class);
    private final String receiveEmail = "receive.1633867@gmail.com";

    /**
     * Send method, takes an email bean as param and sends it to a specified
     * receiver.
     *
     * @param EmailBean bean
     * @version 1.0.0
     */
    @Override
    public void send(EmailBean bean) throws SQLException {
        if (verifyBeanData(bean) && bean != null) {
            
            SmtpModule smtp = new SmtpModule(bean.getFrom(), password);
            smtp.send(bean);
            System.out.println("DONE");
        }
    }

    /**
     * Receive method, returns a processed emailbean array from the IMAP server
     *
     * @return EmailBean[]
     * @version 1.0.0
     */
    @Override
    public EmailBean[] receive() {
        EmailBean[] emails = null;
        if (checkEmail(receiveEmail)) {
            ImapModule imap = new ImapModule("send.1633867@gmail.com", "dawsoncollege");
            emails = imap.receive();
        }
        return emails;
    }

    /** 
        Private method that verifies all the bean's information
        @param EmailBean to be checked
     **/
    private boolean verifyBeanData(EmailBean bean) throws SQLException {

        if (bean.getFrom() == null || !checkEmail(bean.getFrom())) {
            throw new SQLException("Email: " + bean.getFrom() + " is invalid!");
        } else if (bean.getTo() == null || !checkEmail(bean.getTo())) {
           System.out.println(Arrays.toString(bean.getTo()));
           throw new SQLException("Receiver email is invalid!");
        } else if (!checkEmail(bean.getCc())) {
            throw new SQLException("Cc email is invalid!");
        } else if (!checkEmail(bean.getBcc())) {
            throw new SQLException("Bcc email is invalid!");
        }
        return true;
    }

    /** 
        Private method that verifies email addresses. Can accept one or many Strings
        @param String... emails - one or many emails.
     **/
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
