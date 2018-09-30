package com.emailsystem.persistence;

import com.emailsystem.business.MailModule;
import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import java.sql.DriverManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main DAO, responsible for managing the other DAO as well as fetching EmailBeans
 * 
 * @author Itai Bolyasni
 * @version 1.0.0
 */
public class EmailDAO {

    private final String URL = "jdbc:mysql://localhost:3306/EmailDB?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private final String UNAME = "a1633867";
    private final String PASSWORD = "dawson";
    private final static Logger LOG = LoggerFactory.getLogger(EmailDAO.class);

    AttachmentDAO attachDao;
    RecipientDAO recDao;
    FolderDAO folderDao;

    public EmailDAO() {
        attachDao = new AttachmentDAO(URL, UNAME, PASSWORD);
        recDao = new RecipientDAO(URL, UNAME, PASSWORD);
        folderDao = new FolderDAO(URL, UNAME, PASSWORD);
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
     /**
     * A method that creates all the entries that are related to a certain EmailBean
     * 
     * @param email - the EmailBean that will be saved to the database
     * @return int - the amount of rows that were affected.
     * @version 1.0.0
     */
    public int createEmail(EmailBean email) throws SQLException {
        int result;
        String query = "INSERT INTO Emails(senderEmail, subject, textMsg, htmlMsg, folder_id, sentDate) VALUES (?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, email.getFrom());
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
     * A method that creates all the entries that are related to a certain EmailBean
     * 
     * @param newFolderName - the name of the folder where the email will be moved to
     * @param email_id - the id of the email that will be moved to another folder
     * @return int - the amount of rows that were affected.
     * @version 1.0.0
     */
    public int updateEmailFolder(String newFolderName, int email_id) throws SQLException {
        String query = "UPDATE Emails SET folder_id = ? WHERE email_id = ?";
        if (folderDao.getId(newFolderName) == -1) {
            throw new SQLException("Folder you are trying to move your email to doesn't exist!");
        }
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, folderDao.getId(newFolderName));
            ps.setInt(2, email_id);
            return ps.executeUpdate();
        }
    }
     /**
     * A method that fetches a record from the database and transforms it into an EmailBean
     * 
     * @param id - the email_id
     * @return EmailBean - The EmailBean corresponding to the email_id
     * @version 1.0.0
     */
    public EmailBean getEmail(int id) throws SQLException {
        EmailBean bean = new EmailBean();
        String query = "SELECT * FROM Emails e WHERE e.email_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
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
     * @param id - the email_id that the user wishes to delete
     * @return int - the amount of rows that were affected.
     * @version 1.0.0
     */
    public int deleteEmail(int id) throws SQLException {
        String query = "DELETE FROM Emails WHERE email_id = ?";
        int result;
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            result = ps.executeUpdate();
        }
        if (result == 0) {
            throw new SQLException("Record with id: " + id + "doesn't exist!");
        }
        LOG.info("Record with id: " + id + " deleted.");
        return result;
    }

}
