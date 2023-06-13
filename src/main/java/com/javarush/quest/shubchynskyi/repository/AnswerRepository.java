package com.javarush.quest.shubchynskyi.repository;

import com.javarush.quest.shubchynskyi.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
