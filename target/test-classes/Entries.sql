/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  1633867
 * Created: Sep 24, 2018
 */

INSERT INTO EMAILS VALUES (1, "send.1633867@gmail.com", "TEST", "Test msg", "<html><head></head><body><p>Hello</p></body></html>", "folder1", CURRENT_DATE, null);

INSERT INTO RECIPIENTADDRESS(emailAddress, address_id) VALUES ("receive.1633867",1);
INSERT INTO RECIPIENTADDRESS(emailAddress, address_id) VALUES ("cc.1633867@gmail.com", 2);
INSERT INTO RECIPIENTADDRESS(emailAddress, address_id) VALUES ("bcc.1633867@gmail.com", 3);

INSERT INTO RECIPIENT(address_id, type, email_id) VALUES (1, "TO", 1);
INSERT INTO RECIPIENT(address_id, type, email_id) VALUES (2, "CC", 1);
INSERT INTO RECIPIENT(address_id, type, email_id) VALUES (3, "BCC", 1);


SELECT * FROM RecipientAddress ra INNER JOIN Recipient r ON ra.address_id = r.address_id;
