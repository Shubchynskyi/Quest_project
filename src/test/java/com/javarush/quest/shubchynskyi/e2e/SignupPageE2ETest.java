// Обновлённый класс тестов для страницы регистрации
package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.SignupPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.PROFILE_URL;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SignupPageE2ETest extends BaseE2ETest {

    @Test
//    @Order(1)
    @DisplayName("Should register a user successfully")
    public void shouldRegisterUserSuccessfully() {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();

        signupPage.fillLogin("newuser156");
        signupPage.fillPassword("password123");
        signupPage.selectRole("USER");
        signupPage.clickSignUp();

        String currentUrl = driver.getCurrentUrl();
        System.err.println(currentUrl);

        assertTrue(currentUrl.contains(PROFILE_URL), "User was not redirected to /profile.");

        WebElement loginInfo = driver.findElement(By.xpath("//h5[contains(text(), 'Login:')]"));
        WebElement roleInfo = driver.findElement(By.xpath("//h5[contains(text(), 'Role:')]"));
        assertNotNull(loginInfo, "Login info is not displayed.");
        assertNotNull(roleInfo, "Role info is not displayed.");
        assertTrue(loginInfo.getText().contains("newuser"), "Displayed login is incorrect.");
        assertTrue(roleInfo.getText().contains("USER"), "Displayed role is incorrect.");
    }

    @Test
//    @Order(2)
    @DisplayName("Should show error for duplicate login")
    public void shouldShowErrorForDuplicateLogin() {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();

        signupPage.fillLogin("admin");
        signupPage.fillPassword("password123");
        signupPage.selectRole("USER");
        signupPage.clickSignUp();

        assertTrue(signupPage.isErrorDisplayed(), "Error message was not displayed.");
        assertEquals("Login already exists", signupPage.getErrorMessage());
    }

    @Test
//    @Order(3)
    @DisplayName("Should show error for invalid login format")
    public void shouldShowErrorForInvalidLogin() {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();

        signupPage.fillLogin("%%");
        signupPage.fillPassword("password123");
        signupPage.selectRole("USER");
        signupPage.clickSignUp();

        assertTrue(signupPage.isErrorDisplayed(), "Error message was not displayed.");
        assertEquals("Login must be between 3 and 20 characters", signupPage.getErrorMessage());
    }

    @Test
//    @Order(4)
    @DisplayName("Should validate empty fields")
    public void shouldValidateEmptyFields() {
        SignupPage signupPage = new SignupPage(driver, port);
        signupPage.open();

        signupPage.clickSignUp();

        assertTrue(signupPage.isFieldRequired("login"), "Login field is not marked as required.");
        assertTrue(signupPage.isFieldRequired("password"), "Password field is not marked as required.");
    }
}
