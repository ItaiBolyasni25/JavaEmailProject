/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  1633867
 * Created: Sep 19, 2018
 */


USE EmailDB;

DROP USER IF EXISTS a1633867@localhost;
CREATE USER a1633867@'localhost' IDENTIFIED BY 'dawson';
GRANT ALL ON EmailDB.* TO a1633867@'localhost';

-- This creates a user with access from any IP number except localhost
-- Use only if your MyQL database is on a different host from localhost
-- DROP USER IF EXISTS fish; -- % user
-- CREATE USER fish IDENTIFIED BY 'kfstandard';
-- GRANT ALL ON AQUARIUM TO fish@'%';

FLUSH PRIVILEGES;

