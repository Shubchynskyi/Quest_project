package com.javarush.quest.shubchynskyi.dto;

import com.javarush.quest.shubchynskyi.entity.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDTO {
    private Long id;
    private String login;
    private String password;
    private Role role;
    private List<QuestDTO> quests;
    private List<GameDTO> games;
    private List<QuestDTO> questsInGame;

    public String getImage() {
        return "image-" + id;
    }
}
