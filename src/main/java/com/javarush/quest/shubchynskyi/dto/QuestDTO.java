package com.javarush.quest.shubchynskyi.dto;

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
public class QuestDTO {
    private Long id;
    @NotBlank(message = DtoValidationMessages.VALIDATION_QUEST_NAME_NOT_BLANK)
    @Size(min = 3, max = 100, message = DtoValidationMessages.VALIDATION_QUEST_NAME_SIZE)
    private String name;
    @NotBlank(message = DtoValidationMessages.VALIDATION_QUEST_DESCRIPTION_NOT_BLANK)
    @Size(min = 10, max = 200, message = DtoValidationMessages.VALIDATION_QUEST_DESCRIPTION_SIZE)
    private String description;
    private Long startQuestionId;
    private UserDTO author;
    private List<QuestionDTO> questions;
    private List<UserDTO> players;

    public String getImage() {
        return "quest-" + id;
    }
}
