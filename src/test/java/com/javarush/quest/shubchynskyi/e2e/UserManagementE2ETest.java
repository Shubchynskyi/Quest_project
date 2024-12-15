package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.EditUserPage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.IndexPage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.ProfilePage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.SignupPage;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.test_config.ValidationMessageLocalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

import static com.javarush.quest.shubchynskyi.localization.DtoValidationMessages.VALIDATION_SIZE_USER_DTO_LOGIN;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.LOGIN_ALREADY_EXIST;
import static com.javarush.quest.shubchynskyi.test_config.TestConstants.SIGNUP_URL;
import static org.junit.jupiter.api.Assertions.*;

public class UserManagementE2ETest extends BaseE2ETest {

    private WebDriverWait wait;

    @BeforeEach
    public void setUpWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Value("${e2e.validUserData.newUser.login}")
    private String newUserLogin;
    @Value("${e2e.validUserData.newUser.password}")
    private String newUserPassword;
    @Value("${e2e.validUserData.incorrect.login}")
    private String incorrectLogin;

    @Test
    @DisplayName("Should show error for duplicate login")
    public void shouldShowErrorForDuplicateLogin() {
        SignupPage signupPage = registerUser(adminLogin, newUserPassword, userRole);

        assertTrue(driver.getCurrentUrl().contains(SIGNUP_URL), "Expected to remain on /signup after error.");

        assertTrue(signupPage.isErrorDisplayed(), "Error message was not displayed.");
        assertEquals(ErrorLocalizer.getLocalizedMessage(LOGIN_ALREADY_EXIST), signupPage.getErrorMessage());
    }

    @Test
    @DisplayName("Should show error for invalid login format")
    public void shouldShowErrorForInvalidLogin() {
        SignupPage signupPage = registerUser(incorrectLogin, newUserPassword, userRole);

        assertTrue(signupPage.isErrorDisplayed(), "Error message was not displayed.");
        assertEquals(ValidationMessageLocalizer.getValidationFieldMessage(VALIDATION_SIZE_USER_DTO_LOGIN), signupPage.getErrorMessage());
    }

    @Test
    @DisplayName("Should validate empty fields")
    public void shouldValidateEmptyFields() {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();
        signupPage.clickSignUp();

        assertTrue(signupPage.isFieldRequired("login"), "Login field is not marked as required.");
        assertTrue(signupPage.isFieldRequired("password"), "Password field is not marked as required.");
    }

    @Test
    @DisplayName("CRUD operations for user management")
    public void shouldEditAndDeleteUserProfileSuccessfully() {
        registerUser(newUserLogin, newUserPassword, userRole);

        ProfilePage profilePage = new ProfilePage(driver, port);

        assertTrue(profilePage.isOnProfilePage(), "User was not redirected to /profile.");
        assertEquals(newUserLogin, profilePage.getLoginText().replace("Login: ", ""), "Displayed login is incorrect.");
        assertEquals(userRole, profilePage.getRoleText().replace("Role: ", ""), "Displayed role is incorrect.");

        profilePage.clickEditUserButton();

        EditUserPage editUserPage = new EditUserPage(driver, port);
        assertTrue(editUserPage.isOnEditUserPage(), "Not on edit user page.");

        String editedLogin = newUserLogin + "q";
        String editedPassword = newUserPassword + "q";

        editUserPage.fillLogin(editedLogin);
        editUserPage.fillPassword(editedPassword);
        editUserPage.clickSave();

        assertTrue(profilePage.isOnProfilePage(), "User was not redirected to /profile.");
        assertTrue(profilePage.getLoginText().contains(editedLogin), "Updated login is not displayed on profile.");

        profilePage.clickEditUserButton();
        editUserPage = new EditUserPage(driver, port);
        editUserPage.clickDelete();
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        IndexPage layoutPage = new IndexPage(driver, port);

        assertTrue(layoutPage.isOnIndexPage(), "User was not redirected to '/' after deletion.");

        WebElement loginLink = layoutPage.getLoginLink();

        assertNotNull(loginLink, "Login link is not displayed after user deletion.");
    }

    private SignupPage registerUser(String login, String password, String role) {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();
        signupPage.fillLogin(login);
        signupPage.fillPassword(password);
        signupPage.selectRole(role);
        signupPage.clickSignUp();
        return signupPage;
    }

}