DROP SCHEMA IF EXISTS game CASCADE;
CREATE SCHEMA IF NOT EXISTS game;
SET search_path TO game;

CREATE TABLE IF NOT EXISTS game_state
(
    value varchar(64) NOT NULL,
    PRIMARY KEY (value)
);

CREATE TABLE IF NOT EXISTS role
(
    value varchar(128) NOT NULL,
    PRIMARY KEY (value)
);

-- DROP TABLE users;

CREATE TABLE IF NOT EXISTS users
(
    id       bigserial    NOT NULL,
    login    varchar(256) NOT NULL UNIQUE,
    password varchar(256) NOT NULL,
    role     varchar(128) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT users_role_role_value_foreign FOREIGN KEY (role) REFERENCES role (value)
);

CREATE TABLE IF NOT EXISTS game
(
    inr                 integer     NOT NULL,
    quest_id            integer     NOT NULL,
    current_question_id integer     NOT NULL,
    user_id             integer     NOT NULL,
    game_state_value    VARCHAR(64) NOT NULL,
    PRIMARY KEY (inr),
    CONSTRAINT game_user_id_users_id_foreign FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT game_game_state_value_game_state_value_foreign FOREIGN KEY (game_state_value) REFERENCES game_state (value)
);

CREATE TABLE IF NOT EXISTS quest
(
    id                integer       NOT NULL,
    name              varchar(256)  NOT NULL,
    description       varchar(2048) NOT NULL,
    start_question_id integer DEFAULT NULL,
    user_id           integer       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT quest_user_id_users_id_foreign FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS question
(
    id         integer       NOT NULL,
    text       VARCHAR(1024) NOT NULL,
    quest_id   integer       NOT NULL,
    game_state varchar(64)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT question_game_state_game_state_value_foreign FOREIGN KEY (game_state) REFERENCES game_state (value),
    CONSTRAINT question_quest_id_quest_id_foreign FOREIGN KEY (quest_id) REFERENCES quest (id)
);

CREATE TABLE IF NOT EXISTS answer
(
    id               integer       NOT NULL,
    text             varchar(1024) NOT NULL,
    next_question_id integer       NOT NULL,
    column_4         integer       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT answer_next_question_id_question_id_foreign FOREIGN KEY (next_question_id) REFERENCES question (id)
);

INSERT INTO game.role (value) VALUES ('ADMIN');
INSERT INTO game.role (value) VALUES ('USER');
INSERT INTO game.role (value) VALUES ('MODERATOR');
INSERT INTO game.role (value) VALUES ('GUEST');