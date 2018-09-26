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

/**
 *
 * @author 1633867
 */
public class RecipientDAO {

    private final String URL;
    private final String UNAME;
    private final String PASSWORD;

    public RecipientDAO(String URL, String UNAME, String PASSWORD) {
        this.URL = URL;
        this.UNAME = UNAME;
        this.PASSWORD = PASSWORD;
    }

    public int create(EmailBean email, int email_id, String type) throws SQLException {
        String query = "INSERT INTO RecipientAddress(emailAddress) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            switch (type) {
                case "TO":
                    setRecipients(email.getTo(), email_id, ps, type);
                    break;
                case "CC":
                    setRecipients(email.getCc(), email_id, ps, type);
                    break;
                case "BCC":
                    setRecipients(email.getBcc(), email_id, ps, type);
                    break;
                default:
                    throw new SQLException("Invalid email type");
            }
            return 1;
        }
    }

    public String[] read(int id, String type) throws SQLException {
        String query = "SELECT emailAddress FROM Recipient r"
                + " JOIN RecipientAddress ra ON r.address_id = ra.address_id WHERE email_id = ? AND type = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            int result = countRecipientAddresses(id, type);
            String[] emails = new String[result];
            for (int i = 0; i < result; i++) {
                if (rs.next()) {
                    emails[i] = rs.getString("emailAddress");
                }
            }
            return emails;
        }
    }

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
}
