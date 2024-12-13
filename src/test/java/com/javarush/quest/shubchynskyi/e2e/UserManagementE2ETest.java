package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.EditUserPage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.ProfilePage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.SignupPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.INDEX_URL;
import static com.javarush.quest.shubchynskyi.test_config.TestConstants.SIGNUP_URL;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserManagementE2ETest extends BaseE2ETest {

    private WebDriverWait wait;

    @BeforeEach
    public void setUpWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void registerUser(String login, String password, String role) {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();
        signupPage.fillLogin(login);
        signupPage.fillPassword(password);
        signupPage.selectRole(role);
        signupPage.clickSignUp();
    }

    @Test
//    @Order(1)
    public void shouldRegisterUserSuccessfully() {
        registerUser("newuser", "newuser123", "USER");

        ProfilePage profilePage = new ProfilePage(driver, port);
        assertTrue(profilePage.isOnProfilePage(), "Registration failed. Expected redirect to /profile.");

        assertTrue(profilePage.getLoginText().contains("newuser"), "Incorrect login displayed on profile.");
        assertTrue(profilePage.getRoleText().contains("USER"), "Incorrect role displayed on profile.");
    }

    @Test
//    @Order(2)
    public void shouldEditAndDeleteUserProfileSuccessfully() {
        registerUser("edituser", "edituser123", "USER");

        ProfilePage profilePage = new ProfilePage(driver, port);
        assertTrue(profilePage.isOnProfilePage(), "User was not redirected to profile page.");

        profilePage.clickEditUserButton();
        EditUserPage editUserPage = new EditUserPage(driver, port);
        assertTrue(editUserPage.isOnEditUserPage(), "Not on edit user page.");

        editUserPage.fillLogin("editeduser");
        editUserPage.fillPassword("newpassword123");
        editUserPage.clickSave();

        profilePage.open();
        assertTrue(profilePage.getLoginText().contains("editeduser"), "Updated login is not displayed on profile.");

        profilePage.clickEditUserButton();
        editUserPage = new EditUserPage(driver, port);
        editUserPage.clickDelete();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.urlToBe(getBaseUrl() + INDEX_URL));
        assertEquals(getBaseUrl() + INDEX_URL, driver.getCurrentUrl(),
                "User was not redirected to '/' after deletion.");

        WebElement loginLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Login")));
        assertNotNull(loginLink, "Login link is not displayed after user deletion.");
    }

    @Test
//    @Order(4)
    public void shouldShowErrorForExistingLogin() {
        registerUser("admin", "admin123", "USER");

        assertTrue(driver.getCurrentUrl().contains(SIGNUP_URL), "Expected to remain on /signup after error.");

        WebElement errorAlert = driver.findElement(By.className("alert-danger"));
        assertNotNull(errorAlert, "Error message is not displayed.");
        assertEquals("Login already exists", errorAlert.getText());
    }

    @Test
//    @Order(5)
    public void shouldShowErrorForEmptyFields() {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();
        signupPage.clickSignUp();

        assertTrue(signupPage.isFieldRequired("login"), "Login field should be required.");
        assertTrue(signupPage.isFieldRequired("password"), "Password field should be required.");
    }

    @Test
//    @Order(6)
    public void shouldShowErrorForInvalidLoginFormat() {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();

        signupPage.fillLogin("%%");
        signupPage.fillPassword("validpassword123");
        signupPage.selectRole("USER");
        signupPage.clickSignUp();

        WebElement errorAlert = driver.findElement(By.className("alert-danger"));
        assertNotNull(errorAlert, "Error message is not displayed.");
        assertEquals("Login must be between 3 and 20 characters", errorAlert.getText());
    }
}