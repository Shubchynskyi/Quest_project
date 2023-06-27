package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.GameDTO;
import com.javarush.quest.shubchynskyi.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameDTO gameToGameDTO(Game game);
    Game gameDTOToGame(GameDTO gameDTO);
}
