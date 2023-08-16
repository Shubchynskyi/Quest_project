package com.javarush.quest.shubchynskyi.constant;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Key {
    public static final String ID = "id";
    public static final String GAME_STATE = "gameState";

    //**************  Regex  **************//
    public static final String REGEX_EMPTY_STRING = "";
    public static final String REGEX_NEW_LINE = "\n";

    //**************  ImageService  **************//
    public static final String PART_NAME = "image";
    public static final String NO_IMAGE_PNG = "no-image.jpg";
    public static final List<String> EXTENSIONS = List.of(
            ".jpg", ".jpeg", ".png", ".bmp", ".gif", ".webp"
    );

    //**************  URI Patterns  **************//
    public static final String NEXT_QUESTION_URI_PATTERN = "%s?%s=%s&%s=%d";
    public static final String ID_URI_PATTERN = "%s?id=%s";
    public static final String LABEL_URI_PATTERN = "#label-";

    //**************  AppException  **************//
    public static final String INCORRECT_TYPE = "Incorrect type";
    public static final String INCORRECT_STRING = "Incorrect string";
    public static final String INCORRECT_STRING_NUMBER = "Incorrect string number";
    public static final String INCORRECT_TEXT_BLOCK = "Incorrect text block";
    public static final String UNKNOWN_COMMAND = "Unknown command";
    public static final String UNEXPECTED_VALUE = "Unexpected value: ";
    public static final String UNKNOWN_GAME_STATE = "Unknown Game State";

    //**************  View errors  **************//
    public static final String LOGIN_ALREADY_EXIST = "Login already exist";
    public static final String DATA_IS_INCORRECT_PLEASE_CHECK_YOUR_USERNAME_AND_PASSWORD
            = "Data is incorrect, please check your username and password";
    public static final String ERROR = "error";

    //**************  Users  **************//
    public static final String USER = "user";
    public static final String USERS = "users";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ROLES = "roles";
    public static final String CREATE = "create";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String PROFILE = "profile";
    public static final String LOGOUT = "logout";
    public static final String SIGNUP = "signup";
    public static final String SOURCE = "source";

    //**************  Quests  **************//
    public static final String QUEST = "quest";
    public static final String QUESTS = "quests";
    public static final String QUEST_NAME = "questName";
    public static final String QUEST_TEXT = "questText";
    public static final String QUEST_DESCRIPTION = "questDescription";
    public static final String START_QUESTION_ID = "startQuestionId";
    public static final String CREATE_QUEST = "create-quest";
    public static final String QUEST_EDIT = "quest-edit";
    public static final String QUESTS_LIST = "quests-list";

    //**************  Questions  **************//
    public static final String QUESTION = "question";
    public static final String QUESTION_ID = "questionId";
    public static final String QUESTION_TEXT = "questionText";
    public static final String ANSWER = "answer";

    //**************  File Upload and Storage  **************//
    public static final String IMAGE_UPLOAD_ERROR = "Image upload error";
    public static final String PATH_IMAGES = "/images";
    public static final String PATH_IMAGE_NAME = "/{imageName}";
    public static final String PARAM_IMAGE_NAME = "imageName";

}
