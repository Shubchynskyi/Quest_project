package com.javarush.quest.shubchynskyi.mapper;

import com.javarush.quest.shubchynskyi.dto.GameDTO;
import com.javarush.quest.shubchynskyi.entity.Game;
import org.mapstruct.Mapper;

@SuppressWarnings("all")
@Mapper(componentModel = "spring")
public interface GameMapper {

    GameDTO gameToGameDTO(Game game);
    Game gameDTOToGame(GameDTO gameDTO);

}
