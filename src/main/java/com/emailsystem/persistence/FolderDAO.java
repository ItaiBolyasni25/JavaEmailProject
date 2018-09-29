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
public class FolderDAO {
    private final String URL;
    private final String UNAME;
    private final String PASSWORD;

    public FolderDAO(String URL, String UNAME, String PASSWORD) {
        this.URL = URL;
        this.UNAME = UNAME;
        this.PASSWORD = PASSWORD;
    }
    
    public int getId(String folderName) throws SQLException {
        String query = "SELECT folder_id FROM Folders WHERE folderName = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, folderName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }
        }
    }

    public String getFolderName(int id) throws SQLException {
        String query = "SELECT folderName FROM Folders WHERE folder_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new SQLException("ERROR");
            }
        }
    }
    
    public int create(String folderName) throws SQLException {
        if (getId(folderName) != -1) {
            throw new SQLException("Folder " + folderName + " already exists!");
        }
        String query = "INSERT INTO Folders(folderName) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, folderName);
            
            ps.executeUpdate();
            
            int tempId = -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    tempId = rs.getInt(1);
                }
            }
            return tempId;
        }
    }
    
    public int delete(int id) throws SQLException {
        String query = "SELECT folderName FROM Folders WHERE folder_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("folderName").equalsIgnoreCase("Inbox") || rs.getString("folderName").equalsIgnoreCase("Sent")) {
                    throw new SQLException("Cannot delete Inbox or Sent folders");
                }
            }
            return ps.executeUpdate("DELETE FROM Folders WHERE folder_id = " + id);
        }
    }
    
    public int update(String oldFolderName, String newFolderName) throws SQLException {
        if (this.getId(oldFolderName) == -1) {
            throw new SQLException("Folder name doesn't exist!");
        } else if (oldFolderName.equalsIgnoreCase("Inbox") || oldFolderName.equalsIgnoreCase("Sent")) {
            throw new SQLException(oldFolderName + " cannot be deleted!");
        }
        String query = "UPDATE Folders SET folderName = ? WHERE folderName = ?";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newFolderName);
            ps.setString(2, oldFolderName);
            return ps.executeUpdate();
        }
    }
    
}
