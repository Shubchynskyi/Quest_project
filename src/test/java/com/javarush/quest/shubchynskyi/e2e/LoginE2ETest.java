package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.LoginPage;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;

import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.DATA_IS_INCORRECT;
import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginE2ETest extends BaseE2ETest {

    @Value("${e2e.invalidUserData.userLogin}")
    private String invalidUserLogin;
    @Value("${e2e.invalidUserData.userPassword}")
    private String invalidUserPassword;

    private LoginPage loginPage;

    @BeforeEach
    void setUpLoginPage() {
        loginPage = new LoginPage(driver, port);
    }

    @Test
    @DisplayName("Should login as admin successfully")
    void shouldLoginAsAdminSuccessfully() {
        loginAsAdmin();
        assertTrue(driver.getCurrentUrl().contains(PROFILE_URL));
    }

    @Test
    @DisplayName("Should login as moderator successfully")
    void shouldLoginAsModeratorSuccessfully() {
        loginAsModerator();
        assertTrue(driver.getCurrentUrl().contains(PROFILE_URL));
    }

    @Test
    @DisplayName("Should login as user successfully")
    void shouldLoginAsUserSuccessfully() {
        loginAsUser();
        assertTrue(driver.getCurrentUrl().contains(PROFILE_URL));
    }

    @Test
    @DisplayName("Should show error for invalid login")
    void shouldShowErrorForInvalidLogin() {
        loginPage.open();
        loginPage.enterLogin(invalidUserLogin);
        loginPage.enterPassword(invalidUserPassword);
        loginPage.clickLoginButton();
        assertTrue(loginPage.isOnLoginPage());
        assertTrue(loginPage.isErrorDisplayed());
        assertEquals(ErrorLocalizer.getLocalizedMessage(DATA_IS_INCORRECT), loginPage.getErrorMessage());
    }

    @Test
    @DisplayName("Should show error for empty login fields")
    void shouldShowErrorForEmptyLoginFields() {
        loginPage.open();
        loginPage.enterLogin("");
        loginPage.enterPassword("");
        loginPage.clickLoginButton();
        WebElement loginInput = driver.findElement(org.openqa.selenium.By.id("userLogin"));
        WebElement passwordInput = driver.findElement(org.openqa.selenium.By.id("userPassword"));
        assertEquals("true", loginInput.getAttribute("required"));
        assertEquals("true", passwordInput.getAttribute("required"));
    }

    @Test
    @DisplayName("Should redirect to login when accessing protected page without authentication")
    void shouldRedirectToLoginWhenAccessingProtectedPage() {
        driver.get(getBaseUrl() + PROFILE_URL);
        assertTrue(driver.getCurrentUrl().contains(LOGIN_URL));
    }

    @Test
    @DisplayName("Should redirect to login after logout when accessing profile")
    void shouldRedirectToLoginAfterLogoutWhenAccessingProfile() {
        loginAsUser();
        driver.get(getBaseUrl() + LOGOUT_URL);
        assertTrue(driver.getCurrentUrl().contains(INDEX_URL));
        driver.get(getBaseUrl() + PROFILE_URL);
        assertTrue(driver.getCurrentUrl().contains(LOGIN_URL));
    }
}