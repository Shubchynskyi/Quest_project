package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.CREATE_QUEST_URL;

public class CreateQuestPage extends BasePage {

    @FindBy(id = "questName")
    private WebElement questNameField;

    @FindBy(id = "questDescription")
    private WebElement questDescriptionField;

    @FindBy(id = "exampleFormControlTextarea1")
    private WebElement questTextArea;

    @FindBy(id = "submit")
    private WebElement createButton;

    @FindBy(css = "button[data-bs-target='#modal1']")
    private WebElement modalButton1;

    @FindBy(id = "modal1")
    private WebElement modal1;

    @FindBy(css = "#modal1 button.btn-close")
    private WebElement closeModalButton1;

    public CreateQuestPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void open() {
        driver.get(getBaseUrl() + CREATE_QUEST_URL);
        wait.until(ExpectedConditions.visibilityOf(questNameField));
    }

    public void setQuestName(String name) {
        questNameField.clear();
        questNameField.sendKeys(name);
    }

    public void setQuestDescription(String description) {
        questDescriptionField.clear();
        questDescriptionField.sendKeys(description);
    }

    public void openModal1() {
        modalButton1.click();
        wait.until(ExpectedConditions.visibilityOf(modal1));
    }

    public String getModal1Text() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return document.querySelector('#modal1 .modal-body pre').innerText.trim();");
    }

    public void closeModal1() {
        closeModalButton1.click();
        wait.until(ExpectedConditions.invisibilityOf(modal1));
    }

    public void setQuestText(String text) {
        questTextArea.clear();
        questTextArea.sendKeys(text);
    }

    public void clickCreateButton() {
        createButton.click();
    }

    public boolean isFieldRequired(String fieldId) {
        WebElement field = driver.findElement(By.id(fieldId));
        return "true".equals(field.getAttribute("required"));
    }
}