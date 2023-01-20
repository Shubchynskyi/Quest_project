package com.example.quest_project.entity;

//нужен для добавления пользователей в репозиторий, чтобы не любой обьект мог быть в базе, а только те что с интерфейсом

/**
 * Parent for any entity. Use as parent in wildcard
 */
public interface AbstractEntity {
    Long getId();

    void setId();
}
