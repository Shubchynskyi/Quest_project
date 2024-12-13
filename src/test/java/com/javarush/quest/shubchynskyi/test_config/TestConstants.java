package com.javarush.quest.shubchynskyi.test_config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {

    //**************  HTTP and URL Constants  **************//
    public static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    public static final String QUEST_EDIT_ID_URL = "quest-edit?id=";
    public static final String LABEL_URL_FRAGMENT = "#label-";
    public static final String REDIRECT_URL_PATTERN_NEXT_QUESTION = "quest?id=%s&question=*";
    public static final String QUERY_PARAM_ID = "?id=";
    public static final String WILDCARD = "*";
    public static final String SOURCE_URL = "source";
    public static final String INDEX_URL = "/";
    public static final String USER_URL = "/user";
    public static final String PROFILE_URL = "/profile";
    public static final String USERS_URL = "/users";
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String SIGNUP_URL = "/signup";
    public static final String QUEST_URL = "/quest";
    public static final String QUESTS_LIST_URL = "/quests-list";
    public static final String QUEST_EDIT_URL = "/quest-edit";
    public static final String CREATE_QUEST_URL = "/create-quest";
    public static final String QUEST_DELETE_URL = "/quest-delete";

    //**************  File and Directory Constants  **************//
    public static final String TEST_IMAGES_DIR = "target/test-images";
    public static final String TEST_IMAGE_ID = "testImageId";
    public static final String INVALID_TEST_IMAGE_FILE_NAME = TEST_IMAGE_ID + ".txt";
    public static final String VALID_TEST_IMAGE_FILE_NAME = TEST_IMAGE_ID + ".jpeg";
    public static final String MULTIPART_FILE_NAME = "file";

    //**************  Test Data Constants  **************//
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String QUEST_ID = "questId";
    public static final String TEST_QUEST_DESCRIPTION = "Quest Description";
    public static final String TEST_QUEST_TEXT = "Quest text";
    public static final String TEST_QUEST_NAME = "Test Quest";
    public static final String TEST_QUESTION_TEXT = "What is the capital of France?";
    public static final String TEST_ANSWER_TEXT = "Paris";
    public static final String UPDATED_QUEST_NAME = "Updated Quest Name";
    public static final String UPDATED_QUEST_DESCRIPTION = "Updated Quest Description";
    public static final String UPDATED_QUESTION_TEXT = "Updated Question Text";
    public static final String UPDATED_ANSWER_TEXT = "Updated Answer Text";
    public static final String UPDATED_USER_LOGIN = "updatedUser";
    public static final String UPDATED_USER_PASSWORD = "updatedPass";
    public static final String VALID_QUEST_TEXT = """
            1: Test question
            2< Answer to won
            3< Answer to defeat

            3- Defeat

            2+ Won
            """;

    //**************  Validation and Error Messages  **************//
    public static final String ERROR_QUEST_IMAGE_MISMATCH = "The quest image does not match the test image.";
    public static final String ERROR_QUESTION_IMAGE_MISMATCH = "The question image does not match the test image.";
    public static final String ERROR_REDIRECT_URL_MISMATCH = "Redirected URL does not match the expected URL pattern";
    public static final String ASSERT_SESSION_INVALID_AFTER_LOGOUT = "Session should be invalidated after logout";
    public static final String ASSERT_QUEST_NOT_NULL = "Quest should not be null";
    public static final String ASSERT_QUEST_NAME_MATCH = "Quest name should match";
    public static final String ASSERT_QUEST_DESCRIPTION_MATCH = "Quest description should match";
    public static final String ASSERT_AUTHOR_ID_MATCH = "Author ID should match";
    public static final String ASSERT_QUEST_DELETE = "Quest should be deleted.";
    public static final String ASSERT_QUEST_NOT_DELETED = "Quest should not be deleted.";
    public static final String ASSERT_QUEST_REMOVED_FROM_USER_LIST = "Quest should be removed from the user's quest list.";
    public static final String ASSERT_QUEST_REMOVED_FROM_USER_SESSION_LIST = "Quest should be removed from the user's quest list in the session.";
    public static final String ASSERT_QUEST_ADDED_TO_USER_LIST = "Quest should be added to the user's quest list";
    public static final String ASSERT_QUEST_ADDED_TO_USER_SESSION_LIST = "Quest should be added to the user's quest list in the session";
    public static final String ASSERT_UPDATED_USER_NOT_NULL = "Updated user should not be null.";
    public static final String ASSERT_MINIMUM_ONE_LOG = "Must be one log minimum";
    public static final String LOG_EXCEPTION_CAUGHT_IN_CLASS = "Exception caught in class";
    public static final String ASSERT_EXCEPTION_CAUGHT_IN_CLASS = "Must be 'Exception caught in class'";
    public static final String ERROR_TEST_QUEST_NOT_FOUND_WITH_ID = "Test quest not found with ID: ";

    //**************  Exception Constants  **************//
    public static final String ASSERT_APP_EXCEPTION_FOR_MISSING_FILE = "Expected AppException to be thrown if the file does not exist";
    public static final String TEST_EXCEPTION = "Test exception";
    public static final String OBJECT_NAME = "objectName";
    public static final String FIELD = "field";
    public static final String DEFAULT_ERROR_MESSAGE = "defaultMessage";
    public static final String ERROR_MESSAGE = "Localized error message";

    //**************  Identifiers and Numeric Constants  **************//
    public static final Long TEST_USER_ID = 1L;
    public static final String TEST_USER_LOGIN = "testLogin";
    public static final String TEST_USER_PASSWORD = "testPassword";
    public static final String INVALID_USER_PASSWORD = "wrongPassword";
    public static final Long TEST_QUEST_ID = 1L;
    public static final Long NON_EXISTENT_QUEST_ID = 999L;
    public static final String INCORRECT_QUEST_ID = "no id";
    public static final Long TEST_QUESTION_ID = 1L;
    public static final Long TEST_ANSWER_ID = 1L;

}