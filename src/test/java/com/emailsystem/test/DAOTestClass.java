/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.test;


import com.emailsystem.persistence.EmailDAO;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
/**
 *
 * @author 1633867
 */
public class DAOTestClass {
    
    @Test
    public void getEmailTest() {
        EmailDAO db = new EmailDAO();
        try {
            System.out.println(db.getEmail(1));
        } catch (SQLException ex) {
            Logger.getLogger(DAOTestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
