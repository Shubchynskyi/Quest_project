package com.javarush.quest.shubchynskyi.constant;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class Key {
    public static final String ID = "id";
    public static final String GAME_STATE = "gameState";
    public static final String ERROR = "error";
    public static final String IMAGE_ERROR = "imageError";
    public static final String FIELD_ERRORS = "fieldErrors";
    public static final String SLASH = "/";
    public static final String EMPTY_STRING = "";

    //**************  Localization  **************//
    public static final String LOCALIZATION_MESSAGES_BUNDLE = "messages";
    public static final String LOCALE_PARAM_NAME = "lang";
    public static final String ALL_PATHS_PATTERN = "/**";

    //**************  Regex  **************//
    public static final String REGEX_EMPTY_STRING = "";
    public static final String REGEX_NEW_LINE = "\n";
    public static final String QUESTION_REGEX = "^\\d+:.*";
    public static final String ANSWER_REGEX = "^\\d+<.*";
    public static final String WIN_REGEX = "^\\d+\\+.*";
    public static final String LOSE_REGEX = "^\\d+-.*";

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
    public static final String ERROR_UPLOADING_IMAGE = "Error uploading image: ";
    public static final String QUEST_LIST_ERROR = "Quest list is incorrect";
    public static final String UNKNOWN_GAME_STATE = "Unknown Game State";
    public static final String INVALID_FILE_TYPE = "Invalid file type";
    public static final String INVALID_FILE_PATH_ACCESS_DENIED = "Invalid file path, access denied";
    public static final String THE_FILE_PATH_IS_INSECURE = "The file path is insecure";
    public static final String ORIGINAL_FILENAME_IS_NULL_OR_EMPTY = "Original filename is null or empty";
    public static final String FILE_DOES_NOT_EXIST = "File does not exist";
    public static final String FILE_NAME_IS_NULL_OR_EMPTY = "File name is null or empty";
    public static final String USER_NOT_FOUND_WITH_ID = "User not found with id ";
    public static final String QUESTION_NOT_FOUND = "Question not found";

    //**************  Users  **************//
    public static final String USER = "user";
    public static final String USERS = "users";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ROLES = "roles";
    public static final String ROLE = "role";
    public static final String ORIGINAL_LOGIN = "originalLogin";
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
    public static final String QUEST_IMAGE = "questImage";
    public static final String START_QUESTION_ID = "startQuestionId";
    public static final String CREATE_QUEST = "create-quest";
    public static final String QUEST_EDIT = "quest-edit";
    public static final String QUEST_DELETE = "quest-delete";
    public static final String QUESTS_LIST = "quests-list";
    public static final String QUEST_DTO = "questDTO";

    //**************  Questions  **************//
    public static final String QUESTION = "question";
    public static final String QUESTION_ID = "questionId";
    public static final String QUESTION_TEXT = "questionText";
    public static final String ANSWER = "answer";
    public static final String QUESTION_DTO = "questionDTO";

    //**************  File Upload and Storage  **************//
    public static final String IMAGE_UPLOAD_IO_ERROR = "Image upload IO error";
    public static final String PATH_IMAGES = "/images";
    public static final String PATH_IMAGES_TEMP = "/images/temp";
    public static final String PATH_IMAGE_NAME = "/{imageName}";
    public static final String PARAM_IMAGE_NAME = "imageName";
    public static final String IMAGE = "image";
    public static final String MB = "MB";
    public static final int KB_TO_MB = 1024;
    public static final String USER_DTO_FROM_MODEL = "userDTOFromModel";
    public static final String TEMP_IMAGE_ID = "tempImageId";
    public static final int MAX_LENGTH = 100;
    public static final Pattern TEMP_FILE_PATTERN = Pattern.compile("^temp_(\\d+)_.*$");

    public static final String CURRENT_USER_ROLE = "currentUserRole";
    public static final String ACTION_TYPE = "actionType";
    public static final String QUEST_IMAGE_ERROR = "questImageError";
    public static final String QUESTION_IMAGE_ERRORS = "questionImageErrors";
}