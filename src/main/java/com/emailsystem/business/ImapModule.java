package com.emailsystem.business;

import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import java.sql.SQLException;
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
 * ImapModule which is responsible for configuring the IMAP server, as well as
 * receiving en EmailAddress[] and converting it into an EmailBean[]
 *
 * @author Itai Bolyasni
 * @version 1.0.0
 */
public class ImapModule implements MailInterface {

    private final String imapServerName = "imap.gmail.com";
    ImapServer imapServer;
    private final static Logger LOG = LoggerFactory.getLogger(MailModule.class);

    public ImapModule(String receiveEmail, String password) {
        imapServer = MailServer.create()
                .host(imapServerName)
                .ssl(true)
                .auth(receiveEmail, password)
                .buildImapMailServer();
    }

    /**
     *
     * Receive method that takes no params and returns an EmailBean[]
     *
     * @return EmailBean[]
     * @version 1.0.0
     */
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

    /* 
        Private method to process HTML and Plain text messages.
        @param isHtml deciedes the formatting returned.
     */
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

    /* 
        Private method to process attachements and embedded attachments
        @param embed deciedes the formatting returned.
     */
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

    /* 
        Private method thta converts an EmailAddress[] into a String[]
     */
    private String[] emailArrayToStringArray(EmailAddress[] emails) {
        String[] stringEmails = new String[emails.length];
        for (int i = 0; i < emails.length; i++) {
            stringEmails[i] = emails[i].toString();
        }
        return stringEmails;
    }

    @Override
    public void send(EmailBean bean) throws SQLException {
        throw new UnsupportedOperationException("Not supported."); 
    }
}
