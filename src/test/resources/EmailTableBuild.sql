

DROP TABLE IF EXISTS Recipient;
DROP TABLE IF EXISTS RecipientAddress;

DROP TABLE IF EXISTS Attachments;
DROP TABLE IF EXISTS Emails;
DROP TABLE IF EXISTS Folders;



CREATE TABLE Folders (
    folder_id int(10) NOT NULL auto_increment PRIMARY KEY,
    folderName VARCHAR(100) NOT NULL DEFAULT ""
);

CREATE TABLE Emails (
    email_id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    senderEmail VARCHAR(100) NOT NULL DEFAULT "",
    subject VARCHAR(100) NOT NULL DEFAULT "", 
    textMsg LONGTEXT NOT NULL, 
    htmlMsg LONGTEXT NOT NULL, 
    folder_id int(10),
    sentDate TIMESTAMP NOT NULL,
    receivedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (folder_id) REFERENCES Folders(folder_id)
       ON DELETE CASCADE
);

CREATE TABLE Attachments (
    id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    attachName VARCHAR(50) NOT NULL DEFAULT "",
    email_id int(10) NOT NULL ,
    fileArray LONGBLOB NOT NULL,
    isEmbed boolean NOT NULL,
    FOREIGN KEY (email_id) REFERENCES Emails(email_id)
       ON DELETE CASCADE
);

CREATE TABLE RecipientAddress (
    emailAddress VARCHAR(100) NOT NULL,
    address_id int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE Recipient (
    recipientAddress_id int(10) NOT NULL auto_increment PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    email_id int(10) NOT NULL,
    address_id int(10) NOT NULL,
    FOREIGN KEY (email_id) REFERENCES Emails(email_id)
       ON DELETE CASCADE,
    FOREIGN KEY (address_id) REFERENCES RecipientAddress(address_id)
       ON DELETE CASCADE
);

 INSERT INTO Folders(folderName) VALUES ("Inbox");
 INSERT INTO Folders(folderName) VALUES ("Sent");