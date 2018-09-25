    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.test;


import com.emailsystem.data.EmailBean;
import com.emailsystem.persistence.EmailDAO;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
/**
 *
 * @author 1633867
 */
public class DAOTestClass {
    
//    @Test
//    public void getEmailTest() {
//        EmailDAO db = new EmailDAO();
//        try {
//            System.out.println(db.getEmail(1));
//        } catch (SQLException ex) {
//            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    @Test
//    public void deleteEmailTest() {
//        EmailDAO db = new EmailDAO();
//        try {
//            Assert.assertEquals(db.deleteEmail(1), 1);
//        } catch (SQLException ex) {
//            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    @Test
//    public void createEmailTest() {
//        EmailDAO db = new EmailDAO();
//        EmailBean bean = new EmailBean();
//        bean.setFrom("send.1633867gmail.com");
//        bean.setTo(new String[]{"receive.1633867@gmail.com"});
//        bean.setSubject("Sending emails with java test new program");
//        bean.setTextMsg("Test");
//        bean.setCc(new String[]{"send.1633867@gmail.com", "cc2@gmail.com"});
//        bean.setHTMLMsg("<!DOCTYPE HTML><html><head></head><body>" + bean.getTextMsg() + "</body></html>");
//        try {
//            Assert.assertEquals(db.createEmail(bean), 1);
//        } catch (SQLException ex) {
//            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
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
