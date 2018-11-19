DROP DATABASE IF EXISTS Puzzungeon_database;

CREATE DATABASE Puzzungeon_database;

USE Puzzungeon_database;

CREATE TABLE user_table (
  userID int(11) NOT NULL AUTO_INCREMENT,
  username varchar(45) NOT NULL,
  userpassword varchar(45) NOT NULL,
  PRIMARY KEY (userID)
);

CREATE TABLE highscore_table (
  id int(11) NOT NULL AUTO_INCREMENT,
  user1 int(11) NOT NULL,
  user2 int(11) NOT NULL,
  score int(11) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user1) REFERENCES user_table(userID),
  FOREIGN KEY (user2) REFERENCES user_table(userID)
) ;
