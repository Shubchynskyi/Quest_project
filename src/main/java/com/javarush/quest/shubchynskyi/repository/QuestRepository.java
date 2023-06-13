package com.javarush.quest.shubchynskyi.repository;

import com.javarush.quest.shubchynskyi.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QuestRepository extends JpaRepository<Quest, Long> {
}
