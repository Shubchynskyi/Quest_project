package com.javarush.quest.shubchynskyi.dto;

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
    @NotBlank(message = "{validation.quest.name.notBlank}")
    @Size(min = 3, max = 100, message = "{validation.quest.name.size}")
    private String name;
    @NotBlank(message = "{validation.quest.description.notBlank}")
    @Size(min = 10, max = 200, message = "{validation.quest.description.size}")
    private String description;
    private Long startQuestionId;
    private UserDTO author;
    private List<QuestionDTO> questions;
    private List<UserDTO> players;

    public String getImage() {
        return "quest-" + id;
    }
}
