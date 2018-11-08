SET GLOBAL time_zone = '+00:00';
SET time_zone = '+00:00';

DROP DATABASE IF EXISTS EmailDB;
CREATE DATABASE EmailDB;


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

