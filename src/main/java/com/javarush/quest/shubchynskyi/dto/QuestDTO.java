package com.javarush.quest.shubchynskyi.dto;

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
    private String name;
    private String description;
    private Long startQuestionId;
    private UserDTO authorId;
    private List<QuestionDTO> questions;
    private List<UserDTO> players;

    public String getImage() {
        return "quest-" + id;
    }
}
