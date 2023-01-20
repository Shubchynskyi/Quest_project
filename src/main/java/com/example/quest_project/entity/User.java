package com.example.quest_project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getters + setters + equals + hashcode
@NoArgsConstructor // empty constructor
@AllArgsConstructor // constructor with all fields
@Builder
public class User {
    private Long id;
    private String login;
    private String password;
    private Role role;

    public String getImage() {  //TODO move to DTO ???
        return "image-" + id;
    }
}
