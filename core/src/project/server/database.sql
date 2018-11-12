DROP DATABASE IF EXISTS Puzzungeon_database;

CREATE DATABASE Puzzungeon_database;

USE Puzzungeon_database;

CREATE TABLE user_table (
  userID int(11) NOT NULL AUTO_INCREMENT,
  username varchar(45) NOT NULL,
  userpassword varchar(45) NOT NULL,
  PRIMARY KEY (userID,username,userpassword)
);

CREATE TABLE highscore_table (
  user1 varchar(45) NOT NULL,
  user2 varchar(45) NOT NULL,
  score int(11) NOT NULL,
  PRIMARY KEY (user1,user2,score)
) ;



