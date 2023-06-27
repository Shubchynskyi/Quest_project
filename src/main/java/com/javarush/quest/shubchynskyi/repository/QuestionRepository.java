package com.javarush.quest.shubchynskyi.repository;

import com.javarush.quest.shubchynskyi.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QuestionRepository extends JpaRepository<Question, Long> {

}
