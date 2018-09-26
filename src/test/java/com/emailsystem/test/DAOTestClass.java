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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
/**
 *
 * @author 1633867
 */
public class DAOTestClass {
    
    private final String URL = "jdbc:mysql://localhost:3306/EmailDB?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
    private final String UNAME = "a1633867";
    private final String PASSWORD = "dawson";
    
    @Test
    public void getEmailTest() {
        EmailDAO db = new EmailDAO();
        try {
            System.out.println(db.getEmail(1));
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }
    
    @Test
    public void deleteEmailTest() {
        EmailDAO db = new EmailDAO();
        try {
            Assert.assertEquals(db.deleteEmail(1), 1);
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void createEmailTest() {
        EmailDAO db = new EmailDAO();
        EmailBean bean = new EmailBean();
        bean.setFrom("send.1633867gmail.com");
        bean.setTo(new String[]{"receive.1633867@gmail.com"});
        bean.setSubject("TEST");
        bean.setTextMsg("Test");
        bean.setCc(new String[]{"send.1633867@gmail.com", "cc2@gmail.com"});
        bean.setHTMLMsg("<!DOCTYPE HTML><html><head></head><body>" + bean.getTextMsg() + "</body></html>");
        bean.setFolderName("test");
        try {
            new FolderDAO(URL,UNAME,PASSWORD).create(bean);
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<AttachmentBean> attach = new ArrayList<AttachmentBean>();
        try {
            attach.add(new AttachmentBean("java.jpg", Files.readAllBytes(new File("java.jpg").toPath())));
            bean.setAttach(attach);
            bean.setEmbedAttach(attach);
        } catch (IOException e) {

        }
        try {
            Assert.assertEquals(db.createEmail(bean), 1);
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Test
    public void getAllEmailsTest() {
        EmailDAO db = new EmailDAO();
        try {
            for (EmailBean bean: db.findAll()) {
                System.out.println(bean);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
