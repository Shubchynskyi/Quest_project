package com.javarush.quest.shubchynskyi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    @ToString.Exclude
    private User authorId;

    @OneToMany()
    @JoinColumn(name = "quest_id")
    @ToString.Exclude
    private final Collection<Question> questions = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "game",
            joinColumns = @JoinColumn(name = "quest_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id")
    )
    @ToString.Exclude
    private final Collection<User> players = new ArrayList<>();

}
