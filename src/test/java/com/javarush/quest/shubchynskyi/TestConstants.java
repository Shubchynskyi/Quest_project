package com.javarush.quest.shubchynskyi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {

    // todo check if all constants are used
    public static final String REDIRECT_ANY_ID_URI_TEMPLATE = "?id=";
    public static final String REDIRECT_QUERY_ID_TEMPLATE = "?id={id}";
    public static final String EMPTY_STRING = "";
    public static final String NO_ROLES_TO_TEST = "No roles to test.";
    public static final String INVALID = "invalid"; // todo remove?
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String QUEST_EDIT_PATH = "/quest-edit?id=";
    public static final String LABEL_FRAGMENT = "#label-";
}
