package com.javarush.quest.shubchynskyi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question", schema = "game")
public class Question implements AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quest_id")
    private Long questId;

    private String text;

    @Enumerated(value = EnumType.STRING)
    private GameState gameState;

    @OneToMany
    @JoinColumn(name = "question_id")
    @ToString.Exclude
    private final Collection<Answer> answers = new ArrayList<>();

    @Transient
    public String getImage() {
        return "question-" + id;
    }
}
