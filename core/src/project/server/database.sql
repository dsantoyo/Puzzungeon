/*CSCI201 Final Project

Project Name: Puzzungeon
Project Number: 7
Project Category: Game

Daniel Santoyo: dsantoyo@usc.edu USC ID: 6926712177
Hayley Pike: hpike@usc.edu USC ID: 8568149839
Yi(Ian) Sui: ysui@usc.edu USC ID: 2961712187
Ekta Gogri: egogri@usc.edu USC ID: 9607321862
*/


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
  user1 int(11) NOT NULL,
  user2 int(11) NOT NULL,
  score int(11) NOT NULL,
  FOREIGN KEY (user1) REFERENCES user_table(userID),
  FOREIGN KEY (user2) REFERENCES user_table(userID)
) ;
