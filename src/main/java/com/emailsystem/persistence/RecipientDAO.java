/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.persistence;

import com.emailsystem.data.EmailBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Recipient DAO, responsible for managing the recipient and recipientAddress table
 * 
 * @author Itai Bolyasni
 * @version 1.0.0
 */
public class RecipientDAO {

    private final String URL;
    private final String UNAME;
    private final String PASSWORD;
    private final static Logger LOG = LoggerFactory.getLogger(RecipientDAO.class);

    public RecipientDAO(String URL, String UNAME, String PASSWORD) {
        this.URL = URL;
        this.UNAME = UNAME;
        this.PASSWORD = PASSWORD;
    }
    
     /**
     * A method that creates a recipient based on an emailBean
     *
     * @param email - the bean
     * @param email_id - the id of the email associated with the recipient
     * @param type - whether the recipient is of type TO, CC or BCC
     * @return id - the id of the requested folder
     * @version 1.0.0
     */
    public int create(EmailBean email, int email_id, String type) throws SQLException {
        String query = "INSERT INTO RecipientAddress(emailAddress) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            switch (type) {
                case "TO":
                    setRecipients(email.getTo(), email_id, ps, type);
                    LOG.info("Created recipient of type TO");
                    break;
                case "CC":
                    setRecipients(email.getCc(), email_id, ps, type);
                    LOG.info("Created recipient of type CC");
                    break;
                case "BCC":
                    setRecipients(email.getBcc(), email_id, ps, type);
                    LOG.info("Created recipient of type BCC");
                    break;
                default:
                    throw new SQLException("Invalid email type");
            }
            return 1;
        }
    }
    
     /**
     * A method that returns a string[] with the recipients
     *
     * @param email_id - the id of the email 
     * @param type - the type of recipient we wish to read
     * @return String[] - the recipient's email addresses
     * @version 1.0.0
     */
    public String[] read(int email_id, String type) throws SQLException {
        String query = "SELECT ra.emailAddress FROM Recipient r"
                + " JOIN RecipientAddress ra ON r.address_id = ra.address_id WHERE email_id = ? AND type = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, email_id);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            int result = countRecipientAddresses(email_id, type);
            String[] emails = new String[result];
            for (int i = 0; i < result; i++) {
                if (rs.next()) {
                    emails[i] = rs.getString("emailAddress");
                }
            }
            return emails;
        }
    }
    
    
    /* Private Methods */
    
     /**
     * A private helper methods that checks if the address already exists in the table, if not it creates a new entry
     *
     * @param EmailArray - the array of email addresses of the recipient
     * @param email_id - the id of the email
     * @param ps - the PreparedStatement
     * @param type - the type of recipient we wish to set
     * @version 1.0.0
     */
    private void setRecipients(String[] emailArray, int email_id, PreparedStatement ps, String type) throws SQLException {
        for (String emailAddress : emailArray) {
            if (findAddressId(emailAddress) == -1) {
                ps.setString(1, emailAddress);
                ps.executeUpdate();
                setRecipientEntry(email_id, findAddressId(emailAddress), type);
            } else {
                setRecipientEntry(email_id, findAddressId(emailAddress), type);
            }
        }
    }
    
     /**
     * A private helper method that finds the address id based on an emailAddress
     *
     * @param emailAddress - the email that we want the id for
     * @return id - the id of the requested emailAddress
     * @version 1.0.0
     */
    private int findAddressId(String emailAddress) throws SQLException {
        int tempId = -1;
        String query = "SELECT address_id FROM RecipientAddress WHERE emailAddress = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, emailAddress);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tempId = rs.getInt(1);
            }
        }
        return tempId;
    }
    
     /**
     * A private helper method that sets an entry in the recipient table(bridging table)
     *
     * @param email_id - the email id
     * @param address_id - the address_id of the recipient
     * @param type - the type of recipient
     * @return id - the id of the requested folder
     * @version 1.0.0
     */
    private int setRecipientEntry(int email_id, int address_id, String type) throws SQLException {
        int result = -1;
        String query = "INSERT INTO Recipient(type,email_id, address_id) VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps2 = connection.prepareStatement(query)) {
            ps2.setString(1, type);
            ps2.setInt(2, email_id);
            ps2.setInt(3, address_id);

            result = ps2.executeUpdate();
        }
        return result;
    }
    
     /**
     * A private helper method that counts how many recipients there are
     *
     * @param email_id - the email id
     * @param type - the type of recipient
     * @return int - the number of recipients for a given email_id and type
     * @version 1.0.0
     */
    private int countRecipientAddresses(int email_id, String type) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RecipientAddress ra "
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
}
