-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema game
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `game` ;

-- -----------------------------------------------------
-- Schema game
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `game` DEFAULT CHARACTER SET utf8 ;
SHOW WARNINGS;
USE `game` ;

-- -----------------------------------------------------
-- Table `game`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game`.`role` (
    `value` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`value`))
    ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `game`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game`.`users` (
                                              `id` INT NOT NULL AUTO_INCREMENT,
                                              `login` VARCHAR(128) NULL,
    `password` VARCHAR(256) NULL,
    `role` VARCHAR(128) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `login_UNIQUE` (`login` ASC) VISIBLE,
    INDEX `fk_users_role_idx` (`role` ASC) VISIBLE,
    CONSTRAINT `fk_users_role`
    FOREIGN KEY (`role`)
    REFERENCES `game`.`role` (`value`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
    ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `game`.`quest`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game`.`quest` (
                                              `id` INT NOT NULL AUTO_INCREMENT,
                                              `name` VARCHAR(512) NULL,
    `description` VARCHAR(2048) NULL,
    `start_question_id` INT NULL,
    `users_id` INT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_quest_users1_idx` (`users_id` ASC) VISIBLE,
    CONSTRAINT `fk_quest_users`
    FOREIGN KEY (`users_id`)
    REFERENCES `game`.`users` (`id`)
    ON DELETE SET NULL
    ON UPDATE SET NULL)
    ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `game`.`game_state`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game`.`game_state` (
    `value` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`value`))
    ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `game`.`question`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game`.`question` (
                                                 `id` INT NOT NULL AUTO_INCREMENT,
                                                 `text` VARCHAR(1024) NULL,
    `quest_id` INT NOT NULL,
    `game_state` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_question_quest1_idx` (`quest_id` ASC) VISIBLE,
    INDEX `fk_question_game_state1_idx` (`game_state` ASC) VISIBLE,
    CONSTRAINT `fk_question_quest`
    FOREIGN KEY (`quest_id`)
    REFERENCES `game`.`quest` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT `fk_question_game_state`
    FOREIGN KEY (`game_state`)
    REFERENCES `game`.`game_state` (`value`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `game`.`answer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game`.`answer` (
                                               `id` INT NOT NULL AUTO_INCREMENT,
                                               `text` VARCHAR(1024) NULL,
    `next_question_id` INT NULL,
    `question_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_answer_question1_idx` (`question_id` ASC) VISIBLE,
    CONSTRAINT `fk_answer_question`
    FOREIGN KEY (`question_id`)
    REFERENCES `game`.`question` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
    ENGINE = InnoDB;

SHOW WARNINGS;

-- -----------------------------------------------------
-- Table `game`.`game`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `game`.`game` (
                                             `id` INT NOT NULL AUTO_INCREMENT,
                                             `quest_id` INT NULL,
                                             `current_question_id` INT NULL,
                                             `users_id` INT NOT NULL,
                                             `game_state_value` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_game_users1_idx` (`users_id` ASC) VISIBLE,
    INDEX `fk_game_game_state1_idx` (`game_state_value` ASC) VISIBLE,
    CONSTRAINT `fk_game_users`
    FOREIGN KEY (`users_id`)
    REFERENCES `game`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_game_game_state`
    FOREIGN KEY (`game_state_value`)
    REFERENCES `game`.`game_state` (`value`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

SHOW WARNINGS;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `game`.`role`
-- -----------------------------------------------------
START TRANSACTION;
USE `game`;
INSERT INTO `game`.`role` (`value`) VALUES ('ADMIN');
INSERT INTO `game`.`role` (`value`) VALUES ('USER');
INSERT INTO `game`.`role` (`value`) VALUES ('GUEST');
INSERT INTO `game`.`role` (`value`) VALUES ('MODERATOR');

COMMIT;


-- -----------------------------------------------------
-- Data for table `game`.`users`
-- -----------------------------------------------------
START TRANSACTION;
USE `game`;
INSERT INTO `game`.`users` (`id`, `login`, `password`, `role`) VALUES (DEFAULT, 'admin', '456', 'ADMIN');
INSERT INTO `game`.`users` (`id`, `login`, `password`, `role`) VALUES (DEFAULT, 'testUser', '123', 'USER');
INSERT INTO `game`.`users` (`id`, `login`, `password`, `role`) VALUES (DEFAULT, 'moder', '456', 'MODERATOR');
INSERT INTO `game`.`users` (`id`, `login`, `password`, `role`) VALUES (DEFAULT, 'guest', '789', 'GUEST');

COMMIT;


-- -----------------------------------------------------
-- Data for table `game`.`game_state`
-- -----------------------------------------------------
START TRANSACTION;
USE `game`;
INSERT INTO `game`.`game_state` (`value`) VALUES ('PLAY');
INSERT INTO `game`.`game_state` (`value`) VALUES ('WIN');
INSERT INTO `game`.`game_state` (`value`) VALUES ('LOST');

COMMIT;

