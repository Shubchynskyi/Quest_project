package com.javarush.quest.shubchynskyi.dto;

import com.javarush.quest.shubchynskyi.entity.GameState;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QuestionDTO {
    private Long id;
    private Long questId;
    private String text;
    private GameState gameState;
    private List<AnswerDTO> answers;
    public String getImage() {
        return "question-" + id;
    }

}
