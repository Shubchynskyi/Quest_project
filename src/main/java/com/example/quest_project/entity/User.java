package com.example.quest_project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getters + setters + equals + hashcode
@NoArgsConstructor // empty constructor
@AllArgsConstructor // constructor with all fields
public class User {
    private long id;
    private String login;
    private String password;
    private Role role;
}
