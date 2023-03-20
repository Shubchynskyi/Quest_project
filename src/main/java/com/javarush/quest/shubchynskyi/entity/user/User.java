package com.javarush.quest.shubchynskyi.entity.user;

import com.javarush.quest.shubchynskyi.entity.AbstractEntity;
import com.javarush.quest.shubchynskyi.entity.game.Game;
import com.javarush.quest.shubchynskyi.entity.game.Quest;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "users", schema = "game")
public class User implements AbstractEntity {
    @Id // первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // гибер будет заполнять id из базы
    private Long id;
    private String login;
    private String password;
    @Enumerated(EnumType.STRING) // записывает Enum как String
    private Role role;
    @Transient
    private final Collection<Quest> quests = new ArrayList<>();
    @Transient
    private final Collection<Game> games = new ArrayList<>();


    public String getImage() {  //TODO move to DTO ???
        return "image-" + id;
    }
}
