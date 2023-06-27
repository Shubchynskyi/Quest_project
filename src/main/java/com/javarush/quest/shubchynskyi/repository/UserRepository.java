package com.javarush.quest.shubchynskyi.repository;


import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @SuppressWarnings("unused")
    @Query("select u from User u where u.role = :role")
    List<Optional<User>> findUsersByRole(@Param("role") Role role);

}
