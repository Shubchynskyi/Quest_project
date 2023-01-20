package com.example.quest_project.repository;

import com.example.quest_project.entity.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;


// TODO add quest repository with id = quest name
public interface Repository<T> {
    Collection<T> getAll();
    Stream<T> find(T pattern); //можно найти к примеру всех админов, будет искать как фильтр
    T get(long id);
    void create(T entity);
    void update(T entity);
    void delete(T entity);
}
