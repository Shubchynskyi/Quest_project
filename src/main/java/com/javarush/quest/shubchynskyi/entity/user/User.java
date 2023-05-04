package com.javarush.quest.shubchynskyi.entity.user;

import com.javarush.quest.shubchynskyi.entity.AbstractEntity;
import com.javarush.quest.shubchynskyi.entity.game.Game;
import com.javarush.quest.shubchynskyi.entity.game.Quest;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements AbstractEntity {
    private Long id;
    private String login;
    private String password;
    private Role role;
    private final Collection<Quest> quests = new ArrayList<>();
    private final Collection<Game> games = new ArrayList<>();
    public String getImage() {  //TODO move to DTO ???
        return "image-" + id;
    }
}
