package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.SIGNUP_URL;

public class SignupPage extends BasePage {

    @FindBy(id = "login")
    private WebElement loginField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "role")
    private WebElement roleSelect;

    @FindBy(id = "updateOrCreate")
    private WebElement signUpButton;

    @FindBy(className = "alert-danger")
    private WebElement errorAlert;

    public SignupPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void open() {
        driver.get(getBaseUrl() + SIGNUP_URL);
        wait.until(ExpectedConditions.visibilityOf(loginField));
    }

    public void fillLogin(String login) {
        loginField.clear();
        loginField.sendKeys(login);
    }

    public void fillPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void selectRole(String role) {
        new org.openqa.selenium.support.ui.Select(roleSelect).selectByVisibleText(role);
    }

    public void clickSignUp() {
        signUpButton.click();
    }

    public boolean isErrorDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger"))) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return errorAlert.getText();
    }

    public boolean isFieldRequired(String fieldId) {
        var field = driver.findElement(org.openqa.selenium.By.id(fieldId));
        return "true".equals(field.getAttribute("required"));
    }
}
