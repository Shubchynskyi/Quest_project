package com.javarush.quest.shubchynskyi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {


    // todo check if all constants are used

    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String QUEST_EDIT_PATH = "/quest-edit?id=";
    public static final String LABEL_FRAGMENT = "#label-";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String QUEST_TEXT = "questText";
    public static final String UPDATED_QUEST_NAME = "Updated Quest Name";
    public static final String UPDATED_QUEST_DESCRIPTION = "Updated Quest Description";
    public static final String UPDATED_QUESTION_TEXT = "Updated Question Text";
    public static final String UPDATED_ANSWER_TEXT = "Updated Answer Text";
    public static final String THE_QUEST_IMAGE_DOES_NOT_MATCH_THE_TEST_IMAGE = "The quest image does not match the test image.";
    public static final String THE_QUESTION_IMAGE_DOES_NOT_MATCH_THE_TEST_IMAGE = "The question image does not match the test image.";
    public static final String APPEND_LETTER = "a";
    public static final String REDIRECTED_URL_DOES_NOT_MATCH_THE_EXPECTED_URL_PATTERN = "Redirected URL does not match the expected URL pattern";
    public static final String INVALID_GAME_STATE = "Invalid game state";
    public static final String REDIRECT_PATTERN_NEXT_QUESTION = "/quest?id=%s&question=*";

    // todo take them from the test_config
    public static final String GAME_STATE_PLAY = "PLAY";
    public static final String GAME_STATE_WIN = "WIN";

    public static final String SESSION_SHOULD_BE_INVALIDATED_AFTER_LOGOUT = "Session should be invalidated after logout";
    public static final String QUEST_SHOULD_NOT_BE_NULL = "Quest should not be null";
    public static final String QUEST_NAME_SHOULD_MATCH = "Quest name should match";
    public static final String QUEST_DESCRIPTION_SHOULD_MATCH = "Quest description should match";
    public static final String AUTHOR_ID_SHOULD_MATCH = "Author ID should match";

    public static final String TEST_IMAGES_DIRECTORY = "target/test-images";
    public static final String TEST_IMAGE_ID = "testImageId";
    public static final String INVALID_FILE_NAME = TEST_IMAGE_ID + ".txt";
    public static final String VALID_FILE_NAME = TEST_IMAGE_ID + ".jpeg";
    public static final String MULTIPART_FILE_NAME = "file";
    public static final String GENERAL_CONTENT_TYPE = "application/octet-stream";
    public static final String EXPECTED_APP_EXCEPTION_TO_BE_THROWN_IF_THE_FILE_DOES_NOT_EXIST = "Expected AppException to be thrown if the file does not exist";

}