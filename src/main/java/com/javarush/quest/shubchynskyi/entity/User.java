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
    @Transient
//    @ManyToMany
//    @JoinTable(name = "users_game",
//            joinColumns = @JoinColumn(name = "users_id", referencedColumnName = "users_id"),
//            inverseJoinColumns = @JoinColumn(name = "game_id", referencedColumnName = "game_id"))
    private final Collection<Game> games = new ArrayList<>();


    public String getImage() {  //TODO move to DTO ???
        return "image-" + id;
    }
}
