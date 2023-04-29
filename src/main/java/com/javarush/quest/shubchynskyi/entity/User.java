package com.javarush.quest.shubchynskyi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "game")
public class User implements AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany
    @JoinColumn(name = "users_id")
    private List<Quest> quests;
    @OneToMany
    @JoinColumn(name = "users_id")
    @ToString.Exclude
    private final Collection<Game> games = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "game",
            joinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "quest_id", referencedColumnName = "id")
    )
    private final Collection<Quest> questsInGame = new ArrayList<>();


    public String getImage() {  //TODO move to DTO ???
        return "image-" + id;
    }
}
