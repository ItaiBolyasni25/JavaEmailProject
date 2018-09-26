/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.persistence;

import com.emailsystem.data.AttachmentBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 1633867
 */
public class AttachmentDAO {
    private final String URL;
    private final String UNAME;
    private final String PASSWORD;
    
    public AttachmentDAO(String URL, String UNAME, String PASSWORD) {
        this.URL = URL;
        this.UNAME = UNAME;
        this.PASSWORD = PASSWORD;
    }

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

            return ps.executeUpdate();
        }
    }
    
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
