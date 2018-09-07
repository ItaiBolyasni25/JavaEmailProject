/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem;

import jodd.mail.MailServer;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;

/**
 *
 * @author 1633867
 */
public class SmtpModule {
    
    private SmtpServer smtp;
    private final String smtpHost = "smtp.gmail.com";
    
    public SmtpModule(String email, String password) {
        this.smtp = MailServer.create()
            .ssl(true)
            .host(smtpHost)
            .auth(email, password)
            .debugMode(true)
            .buildSmtpMailServer();
    }
    
    public SendMailSession createSession() {
        return smtp.createSession();
    }
}
