package com.javarush.quest.shubchynskyi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {

    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String QUEST_EDIT_PATH = "/quest-edit?id=";
    public static final String LABEL_FRAGMENT = "#label-";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String UPDATED_QUEST_NAME = "Updated Quest Name";
    public static final String UPDATED_QUEST_DESCRIPTION = "Updated Quest Description";
    public static final String UPDATED_QUESTION_TEXT = "Updated Question Text";
    public static final String UPDATED_ANSWER_TEXT = "Updated Answer Text";
    public static final String THE_QUEST_IMAGE_DOES_NOT_MATCH_THE_TEST_IMAGE = "The quest image does not match the test image.";
    public static final String THE_QUESTION_IMAGE_DOES_NOT_MATCH_THE_TEST_IMAGE = "The question image does not match the test image.";
    public static final String APPEND_LETTER = "a";
    public static final String REDIRECTED_URL_DOES_NOT_MATCH_THE_EXPECTED_URL_PATTERN = "Redirected URL does not match the expected URL pattern";
    public static final String REDIRECT_PATTERN_NEXT_QUESTION = "/quest?id=%s&question=*";



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


    public static final String EXPECTED_APP_EXCEPTION_TO_BE_THROWN_IF_THE_FILE_DOES_NOT_EXIST = "Expected AppException to be thrown if the file does not exist";

    public static final String TEST_EXCEPTION = "Test exception";
    public static final String OBJECT_NAME = "objectName";
    public static final String FIELD = "field";
    public static final String DEFAULT_MESSAGE = "defaultMessage";
    public static final String ERROR_MESSAGE = "error message";

    public static final Long TEST_USER_ID = 1L;
    public static final String TEST_LOGIN = "testLogin";
    public static final String TEST_PASSWORD = "testPassword";
    public static final String INVALID_PASSWORD = "wrongPassword";
    public static final String TEMP_IMAGE_ID1 = "tempImageId";

    public static final Long USER_ID = 2L;
    public static final Long QUEST_ID = 1L;
    public static final String NOT_EXIST_QUEST_ID = "5";
    public static final String INVALID_QUEST_ID = "no id";
    public static final String QUEST_NAME_HOLDER = "Quest Name";
    public static final String QUEST_DESCRIPTION_HOLDER = "Quest Description";
    public static final String QUEST_TEXT_HOLDER = "Quest text";
    public static final long QUESTION_ID_HOLDER = 1L;
    public static final String QUESTION_TEXT_HOLDER = "What is the capital of France?";
    public static final long TEST_ANSWER_ID_HOLDER = 1L;
    public static final String TEST_ANSWER_TEST_HOLDER = "Paris";
    public static final String TEST_QUEST_NAME = "Test Quest";
    public static final String VALID_QUEST_TEXT = """
            1: Test question
            2< Answer to won
            3< Answer to defeat

            3- Defeat

            2+ Won
            """;
    public static final String MUST_BE_ONE_LOG_MINIMUM = "Must be one log minimum";
    public static final String EXCEPTION_CAUGHT_IN_CLASS = "Exception caught in class";
    public static final String MUST_BE_EXCEPTION_CAUGHT_IN_CLASS = "Must be 'Exception caught in class'";
    public static final String TEST_QUEST_NOT_FOUND_WITH_ID = "Test quest not found with ID: ";
    public static final String QUEST_SHOULD_BE_DELETED = "Quest should be deleted.";
    public static final String QUEST_SHOULD_NOT_BE_DELETED = "Quest should not be deleted.";
    public static final String QUEST_SHOULD_BE_REMOVED_FROM_THE_USER_S_QUEST_LIST = "Quest should be removed from the user's quest list.";
    public static final String UPDATED_USER_SHOULD_NOT_BE_NULL = "Updated user should not be null.";
    public static final String QUEST_SHOULD_BE_REMOVED_FROM_THE_USER_S_QUEST_LIST_IN_THE_SESSION = "Quest should be removed from the user's quest list in the session.";
    public static final String SOURCE_URL_PLACEHOLDER = "source";
    public static final String QUEST_SHOULD_BE_ADDED_TO_THE_USER_S_QUEST_LIST_IN_THE_SESSION = "Quest should be added to the user's quest list in the session";
    public static final String WILDCARD_PATTERN = "*";
    public static final String QUERY_PARAM_ID_PATTERN = "?id=";
    public static final String QUEST_SHOULD_BE_ADDED_TO_THE_USER_S_QUEST_LIST = "Quest should be added to the user's quest list";

}