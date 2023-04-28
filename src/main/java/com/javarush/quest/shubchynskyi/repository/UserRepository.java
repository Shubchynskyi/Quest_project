package com.javarush.quest.shubchynskyi.repository;

import com.javarush.quest.shubchynskyi.entity.User;
import com.javarush.quest.shubchynskyi.repository.abstract_repo.BaseRepository;

import java.util.stream.Stream;

/**
 *  repository for Users, must be changed in future
 */
public class UserRepository extends BaseRepository<User> {

    @Override
    public Stream<User> find(User pattern) {
        return map.values()
                .stream()
                .filter(user -> nullOrEquals(pattern.getId(), user.getId()))
                .filter(user -> nullOrEquals(pattern.getLogin(), user.getLogin()))
                .filter(user -> nullOrEquals(pattern.getPassword(), user.getPassword()))
                .filter(user -> nullOrEquals(pattern.getRole(), user.getRole()));
    }

}
