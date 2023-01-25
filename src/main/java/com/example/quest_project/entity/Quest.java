package com.example.quest_project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quest implements AbstractEntity {
    private Long id;
    private String name;
    private String text; // для чего?.. инфо о квесте? Описание? Нужна еще форма для заполнения
    private Long authorId;
    private Long startQuestionId;
//    private final Collection<User> players = new ArrayList<>(); // для статистики (сейчас проходят)
    private final Collection<Question> questions = new ArrayList<>();
}
