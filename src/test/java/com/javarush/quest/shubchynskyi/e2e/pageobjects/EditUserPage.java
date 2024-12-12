package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class EditUserPage extends BasePage {

    @FindBy(id = "editUserForm")
    private WebElement editUserForm;

    @FindBy(id = "saveButton")
    private WebElement saveButton;

    @FindBy(id = "cancelButton")
    private WebElement cancelButton;

    @FindBy(id = "login")
    private WebElement loginField;

    @FindBy(id = "password")
    private WebElement passwordField;

//    @FindBy(id = "updateOrCreate")
//    private WebElement saveButton;

    @FindBy(css = "button.btn-danger")
    private WebElement deleteButton;

    public EditUserPage(WebDriver driver, int port) {
        super(driver, port);
        wait.until(ExpectedConditions.visibilityOf(editUserForm));
    }

    public boolean isOnEditUserPage() {
        return getCurrentUrl().contains("/user");
    }

    public void clickSaveButton() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    public void clickCancelButton() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
    }

    public void fillLogin(String login) {
        loginField.clear();
        loginField.sendKeys(login);
    }

    public void fillPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickSave() {
        saveButton.click();
    }

    public void clickDelete() {
        deleteButton.click();
    }
}