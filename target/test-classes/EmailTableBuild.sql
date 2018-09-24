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
DROP TABLE IF EXISTS CcAddress;
DROP TABLE IF EXISTS BccAddress;


CREATE TABLE Emails (
    id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, 
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
    email_id int(10) NOT NULL REFERENCES Emails(id),
    flieArray LONGBLOB NOT NULL
);

CREATE TABLE Recipient (
    id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    email_id int(10) NOT NULL REFERENCES Emails(id)
);

CREATE TABLE RecipientAddress (
    id int(10) NOT NULL auto_increment PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    address_id int(10) NOT NULL REFERENCES Recipient(id)
);
CREATE TABLE CcAddress (
    id int(10) NOT NULL auto_increment PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    address_id int(10) NOT NULL REFERENCES Recipient(id)
);
CREATE TABLE BccAddress (
    id int(10) NOT NULL auto_increment PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    address_id int(10) NOT NULL REFERENCES Recipient(id)
);