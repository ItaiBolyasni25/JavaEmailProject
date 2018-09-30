
package com.emailsystem.persistence;

import com.emailsystem.business.MailModule;
import com.emailsystem.data.AttachmentBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO that communicates with the Attachments table in the local database
 * 
 * @author Itai Bolyasni
 * @version 1.0.0
 */
public class AttachmentDAO {
    private final String URL;
    private final String UNAME;
    private final String PASSWORD;
    private final static Logger LOG = LoggerFactory.getLogger(AttachmentDAO.class);

    
    public AttachmentDAO(String URL, String UNAME, String PASSWORD) {
        this.URL = URL;
        this.UNAME = UNAME;
        this.PASSWORD = PASSWORD;
    }

     /**
     * A method that creates an entry in the Attachments table in the database
     *
     * @param attach - the AttachmentBean
     * @param id - the email id
     * @param isEmbed - if the attachment is embedded or not
     * @return int - the amount of rows that were affected.
     * @version 1.0.0
     */
    public int create(AttachmentBean attach, int id, boolean isEmbed) throws SQLException {
        String query;
        if (isEmbed) {
            query = "INSERT INTO Attachments(attachName, email_id, fileArray, isEmbed) VALUES (?,?,?, true)";
        } else {
            query = "INSERT INTO Attachments(attachName, email_id, fileArray, isEmbed) VALUES (?,?,?, false)";
        }
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, attach.getName());
            ps.setInt(2, id);
            ps.setBytes(3, attach.getAttach());
            LOG.info(attach.getName() + " was created");
            return ps.executeUpdate();
        }
    }
    
    /**
     * A method that takes an email_id and returns a list of all of the associated attachments.
     *
     * @param id - the id of the email
     * @param isEmbed - if the attachment is embedded or not
     * @version 1.0.0
     */
    public List<AttachmentBean> read(int id, boolean isEmbed) throws SQLException{
        List<AttachmentBean> attachList = new ArrayList();
        String query = "SELECT * FROM Attachments WHERE email_id = ? AND isEmbed = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.setBoolean(2, isEmbed);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //Finish setting attachBean, make the list and set the bean in the get
                AttachmentBean attach = new AttachmentBean();
                attach.setName(rs.getString("attachName"));
                attach.setAttach(rs.getBytes("fileArray"));
                attachList.add(attach);
            }
        }
        return attachList;
    }
}
