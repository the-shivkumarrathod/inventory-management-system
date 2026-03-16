CREATE DATABASE inventory_db;

USE inventory_db;

CREATE TABLE items(
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100),
quantity INT,
price DOUBLE
);

CREATE TABLE users(
id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(50),
password VARCHAR(50)
);

INSERT INTO users(username,password)
VALUES('AstraX','mvj@123$');