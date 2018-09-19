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
DROP TABLE IF EXISTS Address;
DROP TABLE IF EXISTS toAddress;
DROP TABLE IF EXISTS ccAddress;
DROP TABLE IF EXISTS bccAddress;


CREATE TABLE Emails (
    id int(10) NOT NULL auto_increment, 
    subject VARCHAR(100) NOT NULL, 
    textMsg CLOB NOT NULL, 
    htmlMsg CLOB NOT NULL, 
    folderName VARCHAR(50),
    sentDate TIMESTAMP NOT NULL,
    receivedDate TIMESTAMP NOT NULL
);
CREATE TABLE Attachments (
    id int(10) NOT NULL auto_increment,
    attachName VARCHAR(50) NOT NULL,
    email_id NUMBER(10) NOT NULL,
    flieArray BLOB NOT NULL
);
CREATE TABLE Address (
    id int(10) NOT NULL auto_increment,
    type VARCHAR(50) NOT NULL,
    email_id NUMBER(10)
);
CREATE TABLE toAddress (
    id int(10) NOT NULL auto_increment,
    type VARCHAR(50) NOT NULL,
    address_id int(10) NOT NULL,
);
CREATE TABLE ccAddress (
    id int(10) NOT NULL auto_increment,
    type VARCHAR(50) NOT NULL,
    address_id int(10) NOT NULL,
);
CREATE TABLE bccAddress (
    id int(10) NOT NULL auto_increment,
    type VARCHAR(50) NOT NULL,
    address_id int(10) NOT NULL,
);