/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem.business;

import com.emailsystem.data.EmailBean;
import java.sql.SQLException;

/**
 *
 * @author Itai Bolyasni
 */
public interface MailInterface {
    public void send(EmailBean bean) throws SQLException;
    public EmailBean[] receive() throws SQLException;
}
