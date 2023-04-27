package com.javarush.quest.shubchynskyi.util;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Key {
    public static final String ID = "id";
    public static final String GAME_STATE = "gameState";
    public static final String NEW_USER_ID = "-1L";


    //**************  Quests  **************//
    public static final String QUEST = "quest";
    public static final String QUESTS = "quests";
    public static final String QUEST_NAME = "questName";
    public static final String QUEST_TEXT = "questText";
    public static final String QUEST_DESCRIPTION = "questDescription";
    public static final String START_QUESTION_ID = "startQuestionId";


    //**************  Questions  **************//
    public static final String QUESTION = "question";
    public static final String QUESTION_ID = "questionId";
    public static final String QUESTION_TEXT = "questionText";
    public static final String ANSWER = "answer";


    //**************  URI Patterns  **************//
    public static final String NEXT_QUESTION_URI_PATTERN = "%s?%s=%s&%s=%d";
    public static final String ID_URI_PATTERN = "%s?id=%s";
    public static final String LABEL_URI_PATTERN = "#label-";


    //**************  Regex  **************//
    public static final String REGEX_SLASH_SIGN = "/";
    public static final String REGEX_EMPTY_STRING = "";
    public static final String REGEX_NEW_LINE = "\n";


    //**************  Users  **************//
    public static final String USER = "user";
    public static final String USERS = "users";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";
    public static final String ROLES = "roles";
    public static final String CREATE = "create";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";


    //**************  util Jsp  **************//
    public static final String WEB_INF_JSP_PATTERN = "WEB-INF/%s.jsp";


    //**************  ImageService  **************//
    public static final String IMAGES_FOLDER = "images";
    public static final String PART_NAME = "image";
    public static final String NO_IMAGE_PNG = "no-image.jpg";
    public static final List<String> EXTENSIONS = List.of(
            ".jpg", ".jpeg", ".png", ".bmp", ".gif", ".webp"
    );

    //**************  AppException  **************//
    public static final String INCORRECT_TYPE = "Incorrect type";
    public static final String INCORRECT_STRING = "Incorrect string";
    public static final String INCORRECT_STRING_NUMBER = "Incorrect string number";
    public static final String INCORRECT_TEXT_BLOCK = "Incorrect text block";
    public static final String UNKNOWN_COMMAND = "Unknown command";
}
