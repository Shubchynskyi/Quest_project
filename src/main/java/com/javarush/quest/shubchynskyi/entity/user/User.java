package com.javarush.quest.shubchynskyi.entity.user;

import com.javarush.quest.shubchynskyi.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements AbstractEntity {
    private Long id;
    private String login;
    private String password;
    private Role role;

    public String getImage() {  //TODO move to DTO ???
        return "image-" + id;
    }
}
