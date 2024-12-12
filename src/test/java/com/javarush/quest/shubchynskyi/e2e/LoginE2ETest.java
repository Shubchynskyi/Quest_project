package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.LoginPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class LoginE2ETest extends BaseE2ETest {

    private LoginPage loginPage;

    @BeforeEach
    void setUpLoginPage() {
        loginPage = new LoginPage(driver, port);
    }

    @Test
    @DisplayName("Should login as admin successfully")
    void shouldLoginAsAdminSuccessfully() {
        loginAsAdmin();
        assertTrue(driver.getCurrentUrl().contains("/profile"));
    }

    @Test
    @DisplayName("Should login as moderator successfully")
    void shouldLoginAsModeratorSuccessfully() {
        loginAsModerator();
        assertTrue(driver.getCurrentUrl().contains("/profile"));
    }

    @Test
    @DisplayName("Should login as user successfully")
    void shouldLoginAsUserSuccessfully() {
        loginAsUser();
        assertTrue(driver.getCurrentUrl().contains("/profile"));
    }

    @Test
    @DisplayName("Should show error for invalid login")
    void shouldShowErrorForInvalidLogin() {
        loginPage.open();
        loginPage.enterLogin("invalidUser");
        loginPage.enterPassword("invalidPassword");
        loginPage.clickLoginButton();
        assertTrue(loginPage.isOnLoginPage());
        assertTrue(loginPage.isErrorDisplayed());
        assertEquals("Data is incorrect, please check your username and password", loginPage.getErrorMessage());
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
        assertTrue("true".equals(loginInput.getAttribute("required")));
        assertTrue("true".equals(passwordInput.getAttribute("required")));
    }

    @Test
    @DisplayName("Should redirect to login when accessing protected page without authentication")
    void shouldRedirectToLoginWhenAccessingProtectedPage() {
        driver.get("http://localhost:" + port + "/profile");
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @DisplayName("Should redirect to login after logout when accessing profile")
    void shouldRedirectToLoginAfterLogoutWhenAccessingProfile() {
        loginAsUser();
        driver.get("http://localhost:" + port + "/logout");
        assertTrue(driver.getCurrentUrl().contains("/"));
        driver.get("http://localhost:" + port + "/profile");
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}