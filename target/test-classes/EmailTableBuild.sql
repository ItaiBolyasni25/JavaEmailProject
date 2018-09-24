/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  1633867
 * Created: Sep 19, 2018
 */

DROP TABLE IF EXISTS Emails;
DROP TABLE IF EXISTS Attachments;
DROP TABLE IF EXISTS Recipient;
DROP TABLE IF EXISTS RecipientAddress;


CREATE TABLE Emails (
    email_id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    senderEmail VARCHAR(100) NOT NULL DEFAULT "",
    subject VARCHAR(100) NOT NULL DEFAULT "", 
    textMsg LONGTEXT NOT NULL, 
    htmlMsg LONGTEXT NOT NULL, 
    folderName VARCHAR(50),
    sentDate TIMESTAMP NOT NULL,
    receivedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Attachments (
    id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    attachName VARCHAR(50) NOT NULL DEFAULT "",
    email_id int(10) NOT NULL REFERENCES Emails(email_id),
    flieArray LONGBLOB NOT NULL
);

CREATE TABLE Recipient (
    recipient_id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    address_id int(10) NOT NULL REFERENCES RecipientAddress(address_id),
    type VARCHAR(50) NOT NULL,
    email_id int(10) NOT NULL REFERENCES Emails(email_id)
);

CREATE TABLE RecipientAddress (
    recipientAddress_id int(10) NOT NULL auto_increment PRIMARY KEY,
    emailAddress VARCHAR(100) NOT NULL,
    address_id int(10) NOT NULL REFERENCES Recipient(address_id)
);