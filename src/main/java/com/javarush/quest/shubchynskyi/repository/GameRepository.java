package com.javarush.quest.shubchynskyi.repository;

import com.javarush.quest.shubchynskyi.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("all")
public interface GameRepository extends JpaRepository<Game, Long> {

}
