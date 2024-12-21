package com.javarush.quest.shubchynskyi.dto;

import com.javarush.quest.shubchynskyi.entity.GameState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GameDTO {
    private Long id;
    private Long questId;
    private Long currentQuestionId;
    private Long userId;
    private GameState gameState;
}
