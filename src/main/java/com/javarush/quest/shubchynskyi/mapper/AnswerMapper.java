package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.AnswerDTO;
import com.javarush.quest.shubchynskyi.entity.Answer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    AnswerDTO answerToAnswerDTO(Answer answer);
    Answer answerDTOToAnswer(AnswerDTO answerDTO);
}
