CREATE TABLE t_user
(
    id       BIGSERIAL PRIMARY KEY,
    login    VARCHAR(128) UNIQUE,
    password VARCHAR(128) NOT NULL,
    role     VARCHAR(32)  NOT NULL
);

create table t_quest
(
    id            bigserial,
    text          varchar(2048),
    game_complete boolean
);

SET search_path TO game;

INSERT INTO role (value)
VALUES ('ADMIN'),
       ('USER'),
       ('MODERATOR'),
       ('GUEST');

INSERT INTO users (login, password, role)
VALUES ('admin2', '123', 'ADMIN'),
       ('guest2', '456', 'GUEST'),
       ('user2', '789', 'USER');

INSERT INTO t_user (login, password, role)
VALUES ('admin', '123', 'ADMIN'),
       ('guest', '456', 'GUEST'),
       ('user', '789', 'USER');


SELECT *
FROM t_user; --all

SELECT id
FROM t_user;

SELECT id, login
FROM t_user;

SELECT id АЙДИ, login ЛОГИН
FROM t_user;

SELECT id, password, password, login
FROM t_user;

SELECT id, login, password
FROM t_user
WHERE id > 1;

SELECT *
FROM t_user
ORDER BY id; --сортировка по id

SELECT *
FROM t_user
ORDER BY id DESC; --сортировка по id в обратном порядке


UPDATE t_user
SET login    = 'psiholirik',
    password = 'qwerty'
WHERE login = 'admin';

DELETE
FROM t_user
WHERE login = 'guest';