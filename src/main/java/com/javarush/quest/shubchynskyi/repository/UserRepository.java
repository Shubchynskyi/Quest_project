package com.javarush.quest.shubchynskyi.repository;


import com.javarush.quest.shubchynskyi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.login = :login")
    Optional<User> findByLogin(@Param("login") String login);

}