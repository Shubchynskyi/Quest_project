package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.CreateQuestPage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.ProfilePage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.QuestEditPage;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.test_config.ValidationMessageLocalizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;

import static com.javarush.quest.shubchynskyi.localization.DtoValidationMessages.VALIDATION_QUEST_DESCRIPTION_SIZE;
import static com.javarush.quest.shubchynskyi.localization.DtoValidationMessages.VALIDATION_QUEST_NAME_SIZE;
import static com.javarush.quest.shubchynskyi.localization.ExceptionErrorMessages.QUEST_TEXT_NOT_VALID;
import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class QuestManagementE2ETest extends BaseE2ETest {

    @Value("${e2e.validQuestData.uniqueQuestName}")
    private String uniqueQuestName;
    @Value("${e2e.validQuestData.uniqueQuestNameDescription}")
    private String uniqueQuestNameDescription;
    @Value("${e2e.invalidQuestData.questText}")
    private String invalidQuestText;

    public static final String randomChar = "Q";

    @Test
    @DisplayName("Should redirect to login when accessing create quest without authentication")
    void shouldRedirectToLoginWhenAccessingCreateQuestWithoutAuthentication() {
        driver.get(getBaseUrl() + CREATE_QUEST_URL);
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(LOGIN_URL));
    }

    @Test
    @DisplayName("Should show validation errors for empty fields")
    void shouldShowValidationErrorsForEmptyFields() {
        loginAsUser();
        CreateQuestPage createQuestPage = new CreateQuestPage(driver, port);
        createQuestPage.open();
        assertTrue(createQuestPage.isFieldRequired("questName"));
        assertTrue(createQuestPage.isFieldRequired("questDescription"));
        assertTrue(createQuestPage.isFieldRequired("exampleFormControlTextarea1"));
    }

    @Test
    @DisplayName("Quest management CRUD test")
    void questManagementCRUDTest() {
        loginAsUser();
        CreateQuestPage createQuestPage = new CreateQuestPage(driver, port);
        createQuestPage.open();
        createQuestPage.setQuestName(uniqueQuestName);
        createQuestPage.setQuestDescription(uniqueQuestNameDescription);
        createQuestPage.openModal1();
        String modalText = createQuestPage.getModal1Text();
        createQuestPage.closeModal1();
        createQuestPage.setQuestText(modalText);
        createQuestPage.clickCreateButton();
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(QUEST_EDIT_URL));

        QuestEditPage questEditPage = new QuestEditPage(driver, port);
        String editedQuestName = uniqueQuestName + randomChar;
        String editedQuestDescription = uniqueQuestNameDescription + randomChar;
        questEditPage.setQuestName(editedQuestName);
        questEditPage.setQuestDescription(editedQuestDescription);
        questEditPage.clickSaveQuestButton();
        currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(QUEST_EDIT_URL));
        assertEquals(editedQuestName, questEditPage.getQuestName());
        assertEquals(editedQuestDescription, questEditPage.getQuestDescription());
        questEditPage.clickToQuestsListButton();
        currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(PROFILE_URL));

        ProfilePage profilePage = new ProfilePage(driver, port);
        List<WebElement> questCards = profilePage.getQuestCards();
        WebElement questCard = questCards.stream()
                .filter(c -> c.findElement(By.className("quest-name")).getText().equals(editedQuestName))
                .findFirst()
                .orElse(null);
        assertNotNull(questCard);

        WebElement deleteButton = questCard.findElement(By.cssSelector(".btn-danger"));
        deleteButton.click();
        driver.switchTo().alert().accept();

        List<WebElement> remainingQuests = driver.findElements(By.xpath(
                String.format("//div[@class='card']//h5[contains(text(), '%s')]", editedQuestName)));
        assertTrue(remainingQuests.isEmpty(), "Quest with the edited name was not deleted.");

    }

    @Test
    @DisplayName("Should show errors for duplicate and invalid quest data")
    void shouldShowErrorsForDuplicateAndInvalidQuestData() {
        loginAsAdmin();
        CreateQuestPage createQuestPage = new CreateQuestPage(driver, port);
        createQuestPage.open();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement questNameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("questName")));
        WebElement questDescriptionField = driver.findElement(By.id("questDescription"));
        WebElement questTextArea = driver.findElement(By.id("exampleFormControlTextarea1"));
        WebElement createButton = driver.findElement(By.id("submit"));

        questNameField.clear();
        questNameField.sendKeys(randomChar);
        questDescriptionField.clear();
        questDescriptionField.sendKeys(uniqueQuestNameDescription);
        WebElement modalButton = driver.findElement(By.cssSelector("button[data-bs-target='#modal1']"));
        modalButton.click();
        WebElement modalWindow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal1")));
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        String modalText = (String) jsExecutor.executeScript("return document.querySelector('#modal1 .modal-body pre').innerText.trim();");
        WebElement closeModalButton = modalWindow.findElement(By.cssSelector("button.btn-close"));
        closeModalButton.click();
        wait.until(ExpectedConditions.invisibilityOf(modalWindow));
        questTextArea.clear();
        questTextArea.sendKeys(modalText);
        createButton.click();
        WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        assertEquals(ValidationMessageLocalizer.getValidationFieldMessage(VALIDATION_QUEST_NAME_SIZE), errorAlert.getText());

        questNameField = driver.findElement(By.id("questName"));
        questDescriptionField = driver.findElement(By.id("questDescription"));
        questTextArea = driver.findElement(By.id("exampleFormControlTextarea1"));
        createButton = driver.findElement(By.id("submit"));
        questNameField.clear();
        questNameField.sendKeys(uniqueQuestName);
        questDescriptionField.clear();
        questDescriptionField.sendKeys(randomChar);
        questTextArea.clear();
        questTextArea.sendKeys(modalText);
        createButton.click();
        errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));

        assertEquals(ValidationMessageLocalizer.getValidationFieldMessage(VALIDATION_QUEST_DESCRIPTION_SIZE), errorAlert.getText());

        questNameField = driver.findElement(By.id("questName"));
        questDescriptionField = driver.findElement(By.id("questDescription"));
        questTextArea = driver.findElement(By.id("exampleFormControlTextarea1"));
        createButton = driver.findElement(By.id("submit"));
        questNameField.clear();
        questNameField.sendKeys(uniqueQuestName);
        questDescriptionField.clear();
        questDescriptionField.sendKeys(uniqueQuestNameDescription);
        questTextArea.clear();
        questTextArea.sendKeys(invalidQuestText);
        createButton.click();
        errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        assertEquals(ErrorLocalizer.getLocalizedMessage(QUEST_TEXT_NOT_VALID), errorAlert.getText());
    }
}