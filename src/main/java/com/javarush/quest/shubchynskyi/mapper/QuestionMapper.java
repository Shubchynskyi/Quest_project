package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.QuestionDTO;
import com.javarush.quest.shubchynskyi.entity.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionDTO questionToQuestionDTO(Question question);

}
