package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface QuestMapper {

    @Named("questToQuestDTOWithOutAuthorId")
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "startQuestionId", source = "startQuestionId"),
            @Mapping(target = "authorId", ignore = true),
            @Mapping(target = "questions", ignore = true),
            @Mapping(target = "players", ignore = true)
    })
    QuestDTO questToQuestDTOWithOutAuthorId(Quest quest);
    Quest questDTOToQuest(QuestDTO questDTO);
}
