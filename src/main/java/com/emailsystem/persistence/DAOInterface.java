/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.persistence;

import com.emailsystem.data.BeanInterface;
import java.sql.SQLException;

/**
 *
 * @author GamingDanik
 */
public interface DAOInterface {
    
    public int create() throws SQLException;
    public BeanInterface get(int id) throws SQLException;
    public int delete(int id) throws SQLException;
    
}
