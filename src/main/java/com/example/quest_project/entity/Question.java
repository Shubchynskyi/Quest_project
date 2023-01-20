package com.example.quest_project.entity;

import lombok.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question implements AbstractEntity{
    private Long id;
    private Long questId;
    private String text;
    private GameState gameState;
    private final Collection<Answer> answers = new ArrayList<>();
    public String getImage() {
        return "quest-" + id;
    }

}
