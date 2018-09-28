/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.test;

import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.persistence.FolderDAO;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1633867
 */
public class DAOTestClass extends Assert {

    private final String URL = "jdbc:mysql://localhost:3306/EmailDB?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private final String UNAME = "a1633867";
    private final String PASSWORD = "dawson";
    EmailDAO db = new EmailDAO();
    EmailBean bean;

    @Before
    public void seedDatabase() {
        final String seedDataScript = loadAsString("EmailTableBuild.sql");
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD)) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
        bean = new EmailBean();
        bean.setFrom("send.1633867gmail.com");
        bean.setTo(new String[]{"receive.1633867@gmail.com"});
        bean.setSubject("TEST");
        bean.setTextMsg("Test");
        bean.setCc(new String[]{"send.1633867@gmail.com", "cc2@gmail.com"});
        bean.setHTMLMsg("<!DOCTYPE HTML><html><head></head><body>" + bean.getTextMsg() + "</body></html>");
        bean.setFolderName("Inbox");
        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        try {
            attach.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            bean.setAttach(attach);
            bean.setEmbedAttach(attach);
        } catch (IOException e) {

        }
        for (int i = 0; i < 10; i++) {
            try {
                db.createEmail(bean);
            } catch (SQLException ex) {
                Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @After
    public void removeRows() {
        final String seedDataScript = loadAsString("EmailDatabaseBuild.sql");
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD)) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop database", e);
        }
    }

    @Test
    public void getEmailTest() {
        try {
            bean.setId(1);
            Assert.assertEquals(db.getEmail(1), bean);
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void deleteEmailTest() {
        try {
            Assert.assertEquals(db.deleteEmail(getAllEmailsCount()), 1);
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void createEmailTest() {
        try {
            Assert.assertEquals(db.createEmail(bean), 1);
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void getAllEmailsTest() {
        try {
            Assert.assertEquals(db.findAll().size(), getAllEmailsCount());
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getAllEmailsCount() throws SQLException {
        int result = 0;
        String query = "SELECT COUNT(email_id) FROM Emails";
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD);
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        }
        return result;
    }

    private String loadAsString(final String path) {
        String s = "";
        File file = new File(path);
        try {
            for (String line: Files.readAllLines(file.toPath())) {
                s += line;
            }
        } catch (IOException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }

    private List<String> splitStatements(Reader reader, String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return statements;
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//")
                || line.startsWith("/*");
    }

}
