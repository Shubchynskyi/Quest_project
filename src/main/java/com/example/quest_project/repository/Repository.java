package com.example.quest_project.repository;

import com.example.quest_project.entity.User;

import java.util.Collection;
import java.util.Optional;


// TODO add quest repository with id = quest name
public interface Repository<T> {
    Collection<User> getAll();
    Optional<T> get(long id);
    void create(T entity);
    void update(T entity);
    void delete(T entity);
}
