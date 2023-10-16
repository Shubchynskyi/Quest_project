package com.javarush.quest.shubchynskyi.dto;

import com.javarush.quest.shubchynskyi.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "{validation.empty.userDTO.login}")
    @Size(min = 3, max = 20, message = "{validation.size.userDTO.login}")
    private String login;

    @NotBlank(message = "{validation.empty.userDTO.password}")
    @Size(min = 6, max = 40, message = "{validation.size.userDTO.password}")
    private String password;
    private Role role;
    private List<QuestDTO> quests;
    private List<GameDTO> games;
    private List<QuestDTO> questsInGame;

    public String getImage() {
        return "image-" + id;
    }
}
