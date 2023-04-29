package com.javarush.quest.shubchynskyi.entity;

import com.javarush.quest.shubchynskyi.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answer", schema = "game")
public class Answer implements AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private Long nextQuestionId;
    @Column(name = "question_id")
    private Long questionId;

}
