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

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "{validation.empty.userDTO.login}")
    @Size(min = 3, max = 20, groups = {OnCreate.class, OnUpdate.class}, message = "{validation.size.userDTO.login}")
    private String login;

    @NotBlank(groups = OnCreate.class, message = "{validation.empty.userDTO.password}")
    @Size(min = 6, max = 40, groups = {OnCreate.class, OnUpdate.class}, message = "{validation.size.userDTO.password}")
    private String password;

    private Role role;
    private List<QuestDTO> quests;
    private List<GameDTO> games;
    private List<QuestDTO> questsInGame;

    public String getImage() {
        return "image-" + id;
    }
}