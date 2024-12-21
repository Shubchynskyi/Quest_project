package com.javarush.quest.shubchynskyi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AnswerDTO {
    private Long id;
    private String text;
    private Long nextQuestionId;
    private Long questionId;
}