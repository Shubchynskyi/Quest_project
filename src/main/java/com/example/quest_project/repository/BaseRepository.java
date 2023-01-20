package com.example.quest_project.repository;

import com.example.quest_project.entity.AbstractEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * repository for Ts, must be changed in future
 */
public abstract class BaseRepository<T extends AbstractEntity> implements Repository<T> {
    //для кадого репозитория будет свой map и свой счетчик (т.к. она не static)
    protected final Map<Long, T> map = new HashMap<>(); // map for all Ts
    public final AtomicLong id = new AtomicLong(0L);  // если сделать static, то id будет для всех сущностей
    // если не static, то у каждого типа сущностей свой набор id

    @Override
    public Collection<T> getAll() { // возвращает всех пользователей
        return map.values();
    }

    @Override
    public T get(long id) {
        return map.get(id);
    }

    @Override
    public void create(T entity) {
        entity.setId(id.incrementAndGet());
        update(entity);
    }

    @Override
    public void update(T entity) {
        map.put(entity.getId(), entity);
    }

    @Override
    public void delete(T entity) {
        map.remove(entity.getId());
    }

    // если в паттерне null, то это поле пропускаем (не ложится в коллекцию метода find)
    protected boolean nullOrEquals(Object patternField, Object repoField) {
        return patternField == null || patternField.equals(repoField);
    }

}
