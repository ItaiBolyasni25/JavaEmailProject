/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.persistence;

import com.emailsystem.data.EmailBean;
import java.sql.DriverManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author 1633867
 */
public class EmailDAO {

    private final String URL = "jdbc:mysql://localhost:3306/EmailDB?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private final String UNAME = "a1633867";
    private final String PASSWORD = "dawson";

    public EmailDAO() {

    }

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
                bean.setTo(getEmailAddresses(bean.getId(), "TO"));
                bean.setCc(getEmailAddresses(bean.getId(), "CC"));
                bean.setBcc(getEmailAddresses(bean.getId(), "BCC"));
                bean.setTextMsg(rs.getString("textMsg"));
                bean.setHTMLMsg(rs.getString("htmlMsg"));

                // Timestamp 
                bean.setSentTime(rs.getTimestamp("sentDate").toLocalDateTime());
                bean.setReceivedTime(rs.getTimestamp("receivedDate").toLocalDateTime());
                allEmails.add(bean);
            }
        }
        return allEmails;
    }

    public int createEmail(EmailBean email) throws SQLException {
        int result;
        String query = "INSERT INTO Emails(senderEmail, subject, textMsg, htmlMsg, folderName, sentDate) VALUES (?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, email.getFrom());
            ps.setString(2, email.getSubject());
            ps.setString(3, email.getTextMsg());
            ps.setString(4, email.getHTMLMsg());
            ps.setString(5, email.getFolderName());
            ps.setTimestamp(6, Timestamp.valueOf(email.getSentTime()));

            // Attachments
//            ps.setBytes(8, email.getAttach().get(0).getAttach());
//            ps.setInt(9, email.getPriority());
//            ps.setTimestamp(10, Timestamp.valueOf(email.getSentTime()));
            result = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                int id = -1;
                if (rs.next()) {
                    id = rs.getInt(1);
                    if (email.getCc().length > 0) {
                        result = setRecipient(email, id, "CC");
                    }
                    if (email.getBcc().length > 0) {
                        result = setRecipient(email, id, "BCC");
                    }
                    if (email.getTo().length > 0) {
                        result = setRecipient(email, id, "TO");
                    }
                }
                email.setId(id);
            }
        }
        return result;
    }

    public EmailBean getEmail(int id) throws SQLException {
        EmailBean bean = new EmailBean();
        String query = "SELECT * FROM Emails e WHERE e.email_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bean.setFrom(rs.getString("senderEmail"));
                bean.setTo(getEmailAddresses(id, "TO"));
                bean.setCc(getEmailAddresses(id, "CC"));
                bean.setBcc(getEmailAddresses(id, "BCC"));
                bean.setTextMsg(rs.getString("textMsg"));
                bean.setHTMLMsg(rs.getString("htmlMsg"));
                bean.setSubject(rs.getString("subject"));
                //Attachments

                bean.setSentTime(rs.getTimestamp("sentDate").toLocalDateTime());
                //bean.setReceivedTime(rs.getTimestamp("receivedDate").toLocalDateTime());
            } else {
                System.out.println("Email with id: " + id + " doesn't exist");
            }
        }
        return bean;
    }

    public int deleteEmail(int id) throws SQLException {
        String query = "DELETE FROM Emails WHERE email_id = ?";
        int result;
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            result = ps.executeUpdate();
        }
        System.out.println("Record with id: " + id + " deleted.");
        return result;
    }

    /* Private helper methods */
    private int countRecipientAddresses(int email_id, String type) throws SQLException {
        String sql = "SELECT COUNT(emailAddress) FROM RecipientAddress ra "
                + "JOIN Recipient r ON ra.address_id = r.address_id "
                + "WHERE email_id = ? AND type = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, email_id);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private String[] getEmailAddresses(int email_id, String type) throws SQLException {
        String sql = "SELECT emailAddress FROM RecipientAddress ra "
                + "JOIN Recipient r ON ra.address_id = r.address_id "
                + "WHERE email_id = ? AND type = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, email_id);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            int result = countRecipientAddresses(email_id, type);
            System.out.println("RESULT: " + result);
            String[] emails = new String[result];
            for (int i = 0; i < result; i++) {
                if (rs.next()) {
                    emails[i] = rs.getString("emailAddress");
                }
            }
            return emails;
        }
    }
    // Set the recipient bridging table and get the generated primary key
    private int setRecipient(EmailBean bean, int id, String type) throws SQLException {

        String query = "INSERT INTO Recipient(type,email_id) VALUES (?,?)";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type);
            ps.setInt(2, id);

            ps.executeUpdate();

            int tempId = -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    tempId = rs.getInt(1);
                }
            }
            return setRecipientAddresses(bean, tempId, type);
        }
    }
    
    // Set the recipient's email addresses depending on the type of recipient
    private int setRecipientAddresses(EmailBean bean, int id, String type) throws SQLException {
        int result = -1;
        String query = "INSERT INTO RecipientAddress(emailAddress,address_id) VALUES (?,?)";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps2 = connection.prepareStatement(query)) {
            switch (type) {
                case "TO":
                    for (String emailAddress : bean.getTo()) {
                        ps2.setString(1, emailAddress);
                        ps2.setInt(2, id);
                        result = ps2.executeUpdate();
                    }
                    break;
                case "CC":
                    for (String emailAddress : bean.getCc()) {
                        ps2.setString(1, emailAddress);
                        ps2.setInt(2, id);
                        result = ps2.executeUpdate();

                    }
                    break;
                case "BCC":
                    for (String emailAddress : bean.getBcc()) {
                        ps2.setString(1, emailAddress);
                        ps2.setInt(2, id);
                        result = ps2.executeUpdate();
                    }
                    break;
            }
        }
        return result;
    }
}
