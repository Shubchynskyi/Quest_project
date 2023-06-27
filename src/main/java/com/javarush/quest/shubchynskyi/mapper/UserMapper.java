package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.UserDTO;
import com.javarush.quest.shubchynskyi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);

    @Named("userToUserDTOWithoutPassword")
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "login", source = "login"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "role", source = "role"),
            @Mapping(target = "quests", ignore = true),
            @Mapping(target = "games", ignore = true),
            @Mapping(target = "questsInGame", ignore = true)
    })
    UserDTO userToUserDTOWithOutCollections(User user);

    User userDTOToUser(UserDTO userDTO);

}
