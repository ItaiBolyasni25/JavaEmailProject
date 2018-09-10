/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emailsystem;

import jodd.mail.ImapServer;
import jodd.mail.MailServer;

/**
 *
 * @author 1633867
 */
public class ImapModule {
    
    private final String imapServerName = "imap.gmail.com";
    
    public ImapModule(String receiveEmail, String password) {
                    ImapServer imapServer = MailServer.create()
                    .host(imapServerName)
                    .ssl(true)
                    .auth(receiveEmail, password)
                    .debugMode(true)
                    .buildImapMailServer();
    }
}
