package com.javarush.quest.shubchynskyi.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Route {

    public static final String REDIRECT = "redirect:";
    public static final String INDEX = "/";
    public static final String INDEX_PAGE = "/index";

    //**************  User  **************//
    public static final String USER = "/user";
    public static final String USER_ID = "/user?id=";
    public static final String PROFILE = "/profile";
    public static final String USERS = "/users";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String SIGNUP = "/signup";

    //**************  Quest  **************//
    public static final String QUEST = "/quest";
    public static final String QUESTS_LIST = "/quests-list";
    public static final String QUEST_EDIT = "/quest-edit";
    public static final String QUEST_EDIT_ID = "/quest-edit?id=";
    public static final String QUEST_CREATE = "/create-quest";

}
