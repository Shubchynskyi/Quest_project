package com.javarush.quest.shubchynskyi.dto;

import com.javarush.quest.shubchynskyi.entity.Role;
import com.javarush.quest.shubchynskyi.localization.DtoValidationMessages;
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

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = DtoValidationMessages.VALIDATION_EMPTY_USER_DTO_LOGIN)
    @Size(min = 3, max = 20, groups = {OnCreate.class, OnUpdate.class}, message = DtoValidationMessages.VALIDATION_SIZE_USER_DTO_LOGIN)
    private String login;

    @NotBlank(groups = OnCreate.class, message = DtoValidationMessages.VALIDATION_EMPTY_USER_DTO_PASSWORD)
    @Size(min = 6, max = 40, groups = {OnCreate.class, OnUpdate.class}, message = DtoValidationMessages.VALIDATION_SIZE_USER_DTO_PASSWORD)
    private String password;

    private Role role;
    private List<QuestDTO> quests;
    private List<GameDTO> games;
    private List<QuestDTO> questsInGame;

    public String getImage() {
        return "image-" + id;
    }
}