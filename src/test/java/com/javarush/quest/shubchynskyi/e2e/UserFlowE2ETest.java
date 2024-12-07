package com.javarush.quest.shubchynskyi.e2e;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserFlowE2ETest extends BaseE2ETest {

    @Value("${e2e.admin.login}")
    String adminLogin;

    @Value("${e2e.admin.password}")
    String adminPassword;

    private void registerUser(String login, String password, String role) {
        driver.get("http://localhost:" + port + "/signup");
        WebElement loginField = driver.findElement(By.id("login"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement roleSelect = driver.findElement(By.id("role"));
        WebElement signupButton = driver.findElement(By.id("updateOrCreate"));

        loginField.clear();
        loginField.sendKeys(login);
        passwordField.clear();
        passwordField.sendKeys(password);
        new Select(roleSelect).selectByVisibleText(role);
        signupButton.click();
    }

    private void loginUser(String login, String password) {
        driver.get("http://localhost:" + port + "/login");
        WebElement loginField = driver.findElement(By.id("userLogin"));
        WebElement passwordField = driver.findElement(By.id("userPassword"));
        WebElement loginButton = driver.findElement(By.id("submit"));

        loginField.clear();
        loginField.sendKeys(login);
        passwordField.clear();
        passwordField.sendKeys(password);
        loginButton.click();
    }

    private void assertErrorDisplayed(String expectedErrorMessage) {
        WebElement errorAlert = driver.findElement(By.className("alert-danger"));
        assertTrue(errorAlert.isDisplayed(), "Error alert is not displayed.");
        assertEquals(expectedErrorMessage, errorAlert.getText());
    }

    @Test
    @Order(1)
    public void shouldShowErrorForExistingLoginOnRegistration() {
        registerUser("admin", "admin123", "USER");
        assertErrorDisplayed("Login already exists");
    }

    @Test
    @Order(2)
    public void shouldRegisterNewUserSuccessfully() {
        registerUser("superadmin", "superadmin123", "ADMIN");
        assertEquals("http://localhost:" + port + "/profile", driver.getCurrentUrl());
    }

    @Test
    @Order(3)
    public void shouldLogoutSuccessfully() {
        loginUser("superadmin", "superadmin123");
        driver.get("http://localhost:" + port + "/logout");
        assertEquals("http://localhost:" + port + "/", driver.getCurrentUrl());
    }

    @Test
    @Order(4)
    public void shouldShowErrorForInvalidLogin() {
        loginUser("wronglogin", "wrongpassword");
        assertErrorDisplayed("Data is incorrect, please check your username and password");
    }

    @Test
    @Order(5)
    public void shouldLoginSuccessfully() {
        loginUser("superadmin", "superadmin123");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.matches("http://localhost:" + port + "/profile(;jsessionid=.*)?"),
                "Unexpected URL: " + currentUrl);
    }

    @Test
    @Order(6)
    public void shouldDisplayProfilePageCorrectly() {
        loginUser(adminLogin, adminPassword);
        driver.get("http://localhost:" + port + "/profile");

        // Проверяем отображение информации о пользователе
        WebElement loginInfo = driver.findElement(By.xpath("//h5[contains(text(), 'Login:')]"));
        WebElement roleInfo = driver.findElement(By.xpath("//h5[contains(text(), 'Role:')]"));
        WebElement profileImage = driver.findElement(By.cssSelector(".profile-image"));

        assertNotNull(loginInfo, "Login information is not displayed.");
        assertNotNull(roleInfo, "Role information is not displayed.");
        assertTrue(profileImage.isDisplayed(), "Profile image is not displayed.");

        assertTrue(loginInfo.getText().contains(adminLogin), "Incorrect login displayed.");
        assertTrue(roleInfo.getText().contains("ADMIN"), "Incorrect role displayed.");

        // Проверяем наличие списка квестов
        WebElement questListTitle = driver.findElement(By.xpath("//p[contains(text(), 'My quests')]"));
        assertNotNull(questListTitle, "Quest list title is not displayed.");

        // Проверяем наличие хотя бы одного квеста
        WebElement firstQuestCard = driver.findElement(By.className("card"));
        assertNotNull(firstQuestCard, "No quests are displayed in the profile.");
    }
}