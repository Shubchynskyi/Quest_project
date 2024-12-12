package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
        driver.get(getBaseUrl() + "/signup");
        wait.until(ExpectedConditions.visibilityOf(loginField));
    }

    public SignupPage fillLogin(String login) {
        loginField.clear();
        loginField.sendKeys(login);
        return this;
    }

    public SignupPage fillPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
        return this;
    }

    public SignupPage selectRole(String role) {
        new org.openqa.selenium.support.ui.Select(roleSelect).selectByVisibleText(role);
        return this;
    }

    public void clickSignUp() {
        signUpButton.click();
    }

    public boolean isErrorDisplayed() {
        try {
            return errorAlert.isDisplayed();
        } catch (Exception e) {
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
