package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = QuestMapper.class)
public interface UserMapper {

    @Named("userToUserDTOWithoutPasswordAndCollections")
    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "quests", ignore = true),
            @Mapping(target = "games", ignore = true),
            @Mapping(target = "questsInGame", ignore = true)
    })
    UserDTO userToUserDTOWithoutPasswordAndCollections(User user);

    @Named("userToUserDTOWithoutPassword")
    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "games", ignore = true),
            @Mapping(target = "questsInGame", ignore = true),
            @Mapping(target = "quests", qualifiedByName = "questToQuestDTOWithOutQuestions"),
    })
    UserDTO userToUserDTOWithoutPassword(User user);

    User userDTOToUser(UserDTO userDTO);

}