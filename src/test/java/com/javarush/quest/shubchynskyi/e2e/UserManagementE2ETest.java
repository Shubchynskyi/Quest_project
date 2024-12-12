

package com.javarush.quest.shubchynskyi.e2e;




import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserManagementE2ETest extends BaseE2ETest {

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

    @Test
    @Order(1)
    public void shouldRegisterUserSuccessfully() {
        registerUser("newuser", "newuser123", "USER");

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/profile"), "Registration failed. Expected redirect to /profile.");

        WebElement loginInfo = driver.findElement(By.xpath("//h5[contains(text(), 'Login:')]"));
        WebElement roleInfo = driver.findElement(By.xpath("//h5[contains(text(), 'Role:')]"));
        assertTrue(loginInfo.getText().contains("newuser"), "Incorrect login displayed on profile.");
        assertTrue(roleInfo.getText().contains("USER"), "Incorrect role displayed on profile.");
    }

    @Test
    @Order(2)
    public void shouldEditAndDeleteUserProfileSuccessfully() {
        registerUser("edituser", "edituser123", "USER");

        // Проверяем, что мы на странице профиля
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/profile"), "User was not redirected to profile page.");

        // Нажимаем на кнопку "Edit"
        WebElement editButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//form[@action='/profile']//button[contains(@class, 'btn-primary')]")
                ));
        editButton.click();

        // Проверяем, что мы на странице редактирования
        WebElement loginField = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("login")));
        loginField.clear();
        loginField.sendKeys("editeduser");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        passwordField.sendKeys("newpassword123");

        // Нажимаем кнопку "Save"
        WebElement saveButton = driver.findElement(By.id("updateOrCreate"));
        saveButton.click();

        // Проверяем, что изменения сохранены
        WebElement loginInfo = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h5[contains(text(), 'Login:')]")));
        assertTrue(loginInfo.getText().contains("editeduser"), "Updated login is not displayed on profile.");

        // Снова нажимаем "Edit" для проверки кнопки "Delete"
        editButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//form[@action='/profile']//button[contains(@class, 'btn-primary')]")
                ));
        editButton.click();

        // Проверяем наличие кнопки "Delete"
        WebElement deleteButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-danger")));
        assertNotNull(deleteButton, "Delete button is not present.");

        // Нажимаем "Delete" и подтверждаем удаление
        deleteButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        // Проверяем, что редирект произошёл на "/"
        boolean isRedirectedToRoot = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlToBe("http://localhost:" + port + "/"));
        assertTrue(isRedirectedToRoot, "User was not redirected to '/' after deletion.");


        // Проверяем, что пользователь больше не в сессии (проверяем доступность ссылки на логин)
        WebElement loginLink = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Login")));
        assertNotNull(loginLink, "Login link is not displayed after user deletion.");
    }


    @Test
    @Order(4)
    public void shouldShowErrorForExistingLogin() {
        registerUser("admin", "admin123", "USER");

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/signup"), "Expected to remain on /signup after error.");

        // Проверяем отображение ошибки
        WebElement errorAlert = driver.findElement(By.className("alert-danger"));
        assertNotNull(errorAlert, "Error message is not displayed.");
        assertEquals("Login already exists", errorAlert.getText());
    }

    @Test
    @Order(5)
    public void shouldShowErrorForEmptyFields() {
        driver.get("http://localhost:" + port + "/signup");
        WebElement signupButton = driver.findElement(By.id("updateOrCreate"));

        WebElement loginField = driver.findElement(By.id("login"));
        WebElement passwordField = driver.findElement(By.id("password"));

        loginField.clear();
        passwordField.clear();
        signupButton.click();

        // Проверяем браузерную валидацию через атрибуты "required"
        assertTrue(loginField.getAttribute("required").equals("true"), "Login field should be required.");
        assertTrue(passwordField.getAttribute("required").equals("true"), "Password field should be required.");
    }

    @Test
    @Order(6)
    public void shouldShowErrorForInvalidLoginFormat() {
        driver.get("http://localhost:" + port + "/signup");
        WebElement loginField = driver.findElement(By.id("login"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement roleSelect = driver.findElement(By.id("role"));
        WebElement signupButton = driver.findElement(By.id("updateOrCreate"));

        loginField.clear();
        loginField.sendKeys("%%"); // Неверный формат логина
        passwordField.clear();
        passwordField.sendKeys("validpassword123");
        new Select(roleSelect).selectByVisibleText("USER");
        signupButton.click();

        // Проверяем сообщение об ошибке
        WebElement errorAlert = driver.findElement(By.className("alert-danger"));
        assertNotNull(errorAlert, "Error message is not displayed.");
        assertEquals("Login must be between 3 and 20 characters", errorAlert.getText());
    }
}
