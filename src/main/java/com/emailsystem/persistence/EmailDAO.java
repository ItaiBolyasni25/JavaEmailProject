package com.emailsystem.persistence;

import com.emailsystem.business.MailModule;
import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import com.emailsystem.data.EmailFXBean;
import java.sql.DriverManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main DAO, responsible for managing the other DAO as well as fetching
 * EmailBeans
 *
 * @author Itai Bolyasni
 * @version 1.0.0
 */
public class EmailDAO {

    private final static Logger LOG = LoggerFactory.getLogger(EmailDAO.class);
    private final String UNAME;
    private final String PASSWORD;
    private final String URL;
    private final AttachmentDAO attachDao;
    private final RecipientDAO recDao;
    private final FolderDAO folderDao;
    private Connection connection;
    private Properties prop;

    public EmailDAO(String uname, String password, String URL) throws SQLException {
        this.UNAME = uname;
        this.PASSWORD = password;
        this.URL = URL;
        attachDao = new AttachmentDAO(UNAME, PASSWORD);
        recDao = new RecipientDAO(URL, UNAME, PASSWORD);
        folderDao = new FolderDAO(URL, UNAME, PASSWORD);
        this.connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
    }

    /**
     * A method that returns all of the emails in the database
     *
     * @return List - a list of EmailBeans
     * @version 1.0.0
     */
    public List<EmailBean> findAll() throws SQLException {
        String query = "SELECT * FROM Emails";
        List<EmailBean> allEmails = new ArrayList();
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD)) {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmailBean bean = new EmailBean();
                bean.setId(rs.getInt("email_id"));
                bean.setFrom(rs.getString("senderEmail"));
                bean.setSubject(rs.getString("subject"));
                bean.setTo(recDao.read(bean.getId(), "TO"));
                bean.setCc(recDao.read(bean.getId(), "CC"));
                bean.setBcc(recDao.read(bean.getId(), "BCC"));
                bean.setTextMsg(rs.getString("textMsg"));
                bean.setHTMLMsg(rs.getString("htmlMsg"));
                bean.setFolderName(folderDao.getFolderName(rs.getInt("folder_id")));
                //Attachments
                bean.setAttach(attachDao.read(bean.getId(), false));
                bean.setEmbedAttach(attachDao.read(bean.getId(), true));

                // Timestamp 
                bean.setSentTime(rs.getTimestamp("sentDate").toLocalDateTime());
                bean.setReceivedTime(rs.getTimestamp("receivedDate").toLocalDateTime());
                allEmails.add(bean);
            }
        }
        return allEmails;
    }
    
    public FolderDAO getFolderDAO() {
        return this.folderDao;
    }

    public List<EmailBean> findEmailsInFolder(String folderName, String senderEmail) throws SQLException {
        List<EmailBean> list = new ArrayList();
        String query = "SELECT * FROM Emails WHERE folder_id = ? AND senderEmail = ? ORDER BY receivedDate DESC";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, folderDao.getId(folderName));
            ps.setString(2, senderEmail);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EmailBean bean = new EmailBean();
                bean.setId(rs.getInt("email_id"));
                bean.setFrom(rs.getString("senderEmail"));
                bean.setSubject(rs.getString("subject"));
                bean.setTo(recDao.read(bean.getId(), "TO"));
                bean.setCc(recDao.read(bean.getId(), "CC"));
                bean.setBcc(recDao.read(bean.getId(), "BCC"));
                bean.setTextMsg(rs.getString("textMsg"));
                bean.setHTMLMsg(rs.getString("htmlMsg"));
                bean.setFolderName(folderDao.getFolderName(rs.getInt("folder_id")));
                //Attachments
                bean.setAttach(attachDao.read(bean.getId(), false));
                bean.setEmbedAttach(attachDao.read(bean.getId(), true));

                // Timestamp 
                bean.setSentTime(rs.getTimestamp("sentDate").toLocalDateTime());
                bean.setReceivedTime(rs.getTimestamp("receivedDate").toLocalDateTime());
                list.add(bean);

            }
            return list;
        }
    }

    public ObservableList<EmailFXBean> findAllFoldersFX() throws SQLException {
        ObservableList<EmailFXBean> list = FXCollections.observableArrayList();
        String query = "SELECT foldername FROM Folders";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmailFXBean fx = new EmailFXBean();
                fx.setFolderName(rs.getString(1));
                list.add(fx);
            }
        }
        return list;
    }

    /**
     * A method that creates all the entries that are related to a certain
     * EmailBean
     *
     * @param email - the EmailBean that will be saved to the database
     * @return int - the amount of rows that were affected.
     * @version 1.0.0
     */
    public int createEmail(EmailBean email) throws SQLException {
        int result;
        String query = "INSERT INTO Emails(senderEmail, subject, textMsg, htmlMsg, folder_id, sentDate) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            if (email.getFrom().contains(">") && email.getFrom().contains("<")) {
                ps.setString(1, email.getFrom().substring(email.getFrom().indexOf("<"), email.getFrom().indexOf(">")));
            } else {
                ps.setString(1, email.getFrom());
            }
            ps.setString(2, email.getSubject());
            ps.setString(3, email.getTextMsg());
            ps.setString(4, email.getHTMLMsg());
            ps.setInt(5, folderDao.getId(email.getFolderName()));
            ps.setTimestamp(6, Timestamp.valueOf(email.getSentTime()));

            result = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                int id = -1;
                if (rs.next()) {
                    id = rs.getInt(1);
                    if (email.getCc().length > 0) {
                        result = recDao.create(email, id, "CC");
                    }
                    if (email.getBcc().length > 0) {
                        result = recDao.create(email, id, "BCC");
                    }
                    if (email.getTo().length > 0) {
                        result = recDao.create(email, id, "TO");
                    }
                    if (email.getAttach().size() > 0) {
                        for (AttachmentBean attach : email.getAttach()) {
                            result = attachDao.create(attach, id, false);
                        }
                        for (AttachmentBean attach : email.getEmbedAttach()) {
                            result = attachDao.create(attach, id, true);
                        }
                    }
                    LOG.info("Email with id " + id + " was created");
                }
                email.setId(id);

            }
        }
        return result;
    }

    /**
     * A method that creates all the entries that are related to a certain
     * EmailBean
     *
     * @param newFolderName - the name of the folder where the email will be
     * moved to
     * @param email_id - the id of the email that will be moved to another
     * folder
     * @return int - the amount of rows that were affected.
     * @version 1.0.0
     */
    public int updateEmailFolder(String newFolderName, int email_id) throws SQLException {
        String query = "UPDATE Emails SET folder_id = ? WHERE email_id = ?";
        if (folderDao.getId(newFolderName) == -1) {
            throw new SQLException("Folder you are trying to move your email to doesn't exist!");
        }
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, folderDao.getId(newFolderName));
            ps.setInt(2, email_id);
            return ps.executeUpdate();
        }
    }

    /**
     * A method that fetches a record from the database and transforms it into
     * an EmailBean
     *
     * @param id - the email_id
     * @return EmailBean - The EmailBean corresponding to the email_id
     * @version 1.0.0
     */
    public EmailBean getEmail(int id) throws SQLException {
        EmailBean bean = new EmailBean();
        String query = "SELECT * FROM Emails e WHERE e.email_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bean.setFrom(rs.getString("senderEmail"));
                bean.setId(id);
                bean.setTo(recDao.read(id, "TO"));
                bean.setCc(recDao.read(id, "CC"));
                bean.setBcc(recDao.read(id, "BCC"));
                bean.setTextMsg(rs.getString("textMsg"));
                bean.setHTMLMsg(rs.getString("htmlMsg"));
                bean.setSubject(rs.getString("subject"));
                bean.setFolderName(folderDao.getFolderName(rs.getInt("folder_id")));
                //Attachments
                bean.setAttach(attachDao.read(id, false));
                bean.setEmbedAttach(attachDao.read(id, true));

                bean.setSentTime(rs.getTimestamp("sentDate").toLocalDateTime());
                bean.setReceivedTime(rs.getTimestamp("receivedDate").toLocalDateTime());
            } else {
                throw new SQLException("Email with id: " + id + " doesn't exist");
            }
        }
        return bean;
    }

    /**
     * A method that deletes an email from the database
     *
     * @param id - the email_id that the user wishes to delete
     * @return int - the amount of rows that were affected.
     * @version 1.0.0
     */
    public int delete(int id) throws SQLException {
        String query = "DELETE FROM Emails WHERE email_id = ?";
        int result;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            result = ps.executeUpdate();
        }
        if (result == 0) {
            throw new SQLException("Record with id: " + id + "doesn't exist!");
        }
        LOG.info("Record with id: " + id + " deleted.");
        return result;
    }

    public void setProperties(Properties prop) {
        this.prop = prop;
    }
}
