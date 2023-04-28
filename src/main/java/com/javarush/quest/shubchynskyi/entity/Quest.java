package com.javarush.quest.shubchynskyi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quest", schema = "game")
public class Quest implements AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Long startQuestionId;
    @Column(name = "users_id")
    private Long authorId;
    @OneToMany
    @JoinColumn(name = "quest_id")
    private List<Question> questions;
}
