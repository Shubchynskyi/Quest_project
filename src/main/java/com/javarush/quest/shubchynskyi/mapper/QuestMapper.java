package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.QuestDTO;
import com.javarush.quest.shubchynskyi.entity.Quest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface QuestMapper {

    @Named("questToQuestDTOWithOutQuestions")
    @Mappings({
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "questions", ignore = true),
            @Mapping(target = "players", ignore = true)
    })
    QuestDTO questToQuestDTOWithOutQuestions(Quest quest);

    @Named("questToQuestDTO")
    @Mappings({
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "players", ignore = true)
    })
    QuestDTO questToQuestDTO(Quest quest);

}