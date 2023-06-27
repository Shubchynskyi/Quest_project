package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuestMapper {
    QuestMapper INSTANCE = Mappers.getMapper(QuestMapper.class);

    QuestDTO questToQuestDTO(Quest quest);
    Quest questDTOToQuest(QuestDTO questDTO);
}
