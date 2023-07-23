package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = QuestMapper.class)
public interface UserMapper {

    UserDTO userToUserDTO(User user);

    @Named("userToUserDTOWithoutPasswordAndCollections")
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "login", source = "login"),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "role", source = "role"),
            @Mapping(target = "quests", ignore = true),
            @Mapping(target = "games", ignore = true),
            @Mapping(target = "questsInGame", ignore = true)
    })
    UserDTO userToUserDTOWithoutPasswordAndCollections(User user);

    @Named("userToUserDTOWithoutCollections")
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "login", source = "login"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "role", source = "role"),
            @Mapping(target = "quests", ignore = true),
            @Mapping(target = "games", ignore = true),
            @Mapping(target = "questsInGame", ignore = true)
    })
    UserDTO userToUserDTOWithoutCollections(User user);
    @Named("userToUserDTOWithoutPassword")
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "login", source = "login"),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "role", source = "role"),
            @Mapping(target = "quests", qualifiedByName = "questToQuestDTOWithOutAuthorId"),
            @Mapping(target = "games", source = "games"),
            @Mapping(target = "questsInGame", source = "questsInGame")
    })
    UserDTO userToUserDTOWithoutPassword(User user);

    User userDTOToUser(UserDTO userDTO);

}
