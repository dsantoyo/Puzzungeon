DROP DATABASE IF EXISTS game_database;

CREATE DATABASE game_database;

USE game_database;

CREATE TABLE `highscore_table` (
  `user1` int(11) NOT NULL,
  `user2` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  PRIMARY KEY (`user1`,`user2`,`score`),
  KEY `user2` (`user2`),
  CONSTRAINT `highscore_table_ibfk_1` FOREIGN KEY (`user1`) REFERENCES `user_table` (`userID`),
  CONSTRAINT `highscore_table_ibfk_2` FOREIGN KEY (`user2`) REFERENCES `user_table` (`userID`)
) ;

CREATE TABLE `user_table` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `userpassword` varchar(45) NOT NULL,
  PRIMARY KEY (`userID`,`username`,`userpassword`)
);
