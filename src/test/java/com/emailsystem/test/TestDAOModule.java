/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.test;

import com.emailsystem.data.AttachmentBean;
import com.emailsystem.data.EmailBean;
import com.emailsystem.persistence.AttachmentDAO;
import com.emailsystem.persistence.EmailDAO;
import com.emailsystem.persistence.FolderDAO;
import com.emailsystem.persistence.RecipientDAO;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author 1633867
 */
public class TestDAOModule extends Assert {

    private final String URL = "jdbc:mysql://localhost:3306/EmailDB?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&useLegacyDatetimeCode=false";
    private final String UNAME = "a1633867";
    private final String PASSWORD = "dawson";

    EmailDAO db = new EmailDAO();
    EmailBean bean = new EmailBean();
    FolderDAO folders = new FolderDAO(URL, UNAME, PASSWORD);
    AttachmentDAO attachments = new AttachmentDAO(URL, UNAME, PASSWORD);
    RecipientDAO recipients = new RecipientDAO(URL,UNAME,PASSWORD);

    @Before
    public void seedDatabase() {
        final String seedDataScript = loadAsString("EmailTableBuild.sql");
        final String seedEntriesScript = loadAsString("EmailEntriesBuild.sql");
        try (Connection connection = DriverManager.getConnection(URL, UNAME, PASSWORD)) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
            for (String statement : splitStatements(new StringReader(seedEntriesScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
        bean.setFrom("send.1633867@gmail.com");
        bean.setTo(new String[]{"receive.1633867@gmail.com"});
        bean.setSubject("Hello");
        bean.setTextMsg("test");
        bean.setHTMLMsg("test");
        bean.setFolderName("Inbox");
    }
    
    
    /**
     * The database is recreated before each test. If the last test is
     * destructive then the database is in an unstable state. @AfterClass is
     * called just once when the test class is finished with by the JUnit
     * framework. It is instantiating the test class anonymously so that it can
     * execute its non-static seedDatabase routine.
     */
    @AfterClass
    public static void seedAfterTestCompleted() {
        new TestDAOModule().seedDatabase();
    }


    @Test
    public void getEmailTest() throws SQLException {
        bean.setId(1);
        Assert.assertEquals(db.getEmail(1), bean);
    }

    @Test(expected = SQLException.class)
    public void getInexistantEmailTest() throws SQLException {
        db.getEmail(0);
    }

    @Test(expected = SQLException.class)
    public void deleteInexistantEmailTest() throws SQLException {
        db.deleteEmail(100);
    }

    @Test(expected = SQLException.class)
    public void deleteEmailTest() throws SQLException {
        db.deleteEmail(5);
        //GetEmail should throw an exception because the entry doesn't exist
        db.getEmail(5);
    }

    @Test
    public void createEmailTest() throws SQLException {
        db.createEmail(bean);
        //Since the email table is populated with 25 entries, the email that is created will have the id of 26
        Assert.assertEquals(db.getEmail(26), bean);
    }

    @Test
    public void getAllEmailsTest() throws SQLException {
        Assert.assertEquals(db.findAll().size(), getAllEmailsCount());
    }

    @Test
    public void createNewFolderTest() throws SQLException {
        folders.create("Test Folder");
        //Newly created folder id will be 3 because we have Inbox and Sent
        assertEquals(folders.getId("Test Folder"), 3);
    }

    @Test(expected = SQLException.class)
    public void createFolderThatExistsTest() throws SQLException {
        folders.create("Inbox");
    }

    @Test(expected = SQLException.class)
    public void deleteInboxFolder() throws SQLException {
        folders.delete(folders.getId("Inbox"));
    }

    @Test
    public void deleteNormalFolder() throws SQLException {
        folders.create("Test Folder 2");
        folders.delete(folders.getId("Test Folder 2"));
        //Should return -1 if folder doesn't exist
        assertEquals(folders.getId("Test Folder 2"), -1);
    }

    @Test
    public void updateFolderName() throws SQLException {
        folders.create("Test Folder");
        folders.update("Test Folder", "Did it work");
        assertEquals(folders.getFolderName(3), "Did it work");
    }

    @Test(expected = SQLException.class)
    public void updateInboxFolderName() throws SQLException {
        folders.update("Inbox", "Shoudlnt work");
    }

    @Test
    public void createEmbedAttachmentEntry() throws SQLException {
        List<AttachmentBean> attach = new ArrayList();
        try {
            attach.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
        } catch (IOException ex) {
            Logger.getLogger(TestDAOModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        attachments.create(attach.get(0), 1, true);
        assertEquals(attachments.read(1, true), attach);
    }

    @Test
    public void createNormalAttachmentEntry() throws SQLException {
        List<AttachmentBean> attach = new ArrayList();
        try {
            attach.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
        } catch (IOException ex) {
            Logger.getLogger(TestDAOModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        attachments.create(attach.get(0), 1, false);
        assertEquals(attachments.read(1, false), attach);
    }
    
    @Test
    public void createRecipient() throws SQLException {
        // new email will have no recipients that were CC
        db.createEmail(bean);
        // Add cc recipient to newly created email
        bean.setCc(new String[]{"cc2@gmail.com"});
        recipients.create(bean, 26, "CC");
        assertArrayEquals(db.getEmail(26).getCc(), recipients.read(26, "CC"));
    }
    
    @Test
    public void readInexsistantRecipient() throws SQLException {
        assertEquals(recipients.read(100, "TO").length,0);
    }
    
    @Test
    public void deleteAllEmailsWithinFolder() throws SQLException {
        folders.create("toBeDeleted");
        bean.setFolderName("toBeDeleted");
        // Create two emails for testing
        db.createEmail(bean);
        db.createEmail(bean);
        
        folders.delete(3);
        // Deleting the folder should delete all the emails in it, findAll will return 25 instead of 27
        assertEquals(db.findAll().size(), 25);
    }
    
    @Test
    public void updateEmailFolder() throws SQLException {
        folders.create("temp");
        db.updateEmailFolder("temp", 5);
        assertEquals(db.getEmail(5).getFolderName(),"temp");
    }
    
    @Test(expected=SQLException.class)
    public void updateInvalidEmailFolder() throws SQLException {
        // Folder temp doesn't exist
        db.updateEmailFolder("temp", 5);
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
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(path);
                Scanner scanner = new Scanner(inputStream);) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
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
            Logger.getLogger(TestDAOModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        return statements;
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//") || line.startsWith("/*");
    }

}
