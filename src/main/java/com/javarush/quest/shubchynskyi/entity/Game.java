package com.javarush.quest.shubchynskyi.entity;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game", schema = "game")
public class Game implements AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long questId;
    private Long currentQuestionId;
    @Column(name = "users_id")
    private Long userId;
    @Enumerated(value = EnumType.STRING)
    private GameState gameState;
}
