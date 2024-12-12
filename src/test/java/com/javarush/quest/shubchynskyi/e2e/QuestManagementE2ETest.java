package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.CreateQuestPage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.QuestEditPage;
import com.javarush.quest.shubchynskyi.e2e.pageobjects.ProfilePage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestManagementE2ETest extends BaseE2ETest {

    @Test
//    @Order(1)
    @DisplayName("Should redirect to login when accessing create quest without authentication")
    void shouldRedirectToLoginWhenAccessingCreateQuestWithoutAuthentication() {
        driver.get(getBaseUrl() + "/create-quest");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"));
    }

    @Test
//    @Order(2)
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
//    @Order(3)
    @DisplayName("Quest management CRUD test")
    void questManagementCRUDTest() {
        loginAsUser();
        CreateQuestPage createQuestPage = new CreateQuestPage(driver, port);
        createQuestPage.open();
        createQuestPage.setQuestName("Unique Quest");
        createQuestPage.setQuestDescription("Unique Quest Description");
        createQuestPage.openModal1();
        String modalText = createQuestPage.getModal1Text();
        createQuestPage.closeModal1();
        createQuestPage.setQuestText(modalText);
        createQuestPage.clickCreateButton();
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/quest-edit"));

        QuestEditPage questEditPage = new QuestEditPage(driver, port);
        questEditPage.setQuestName("Edited Quest Name");
        questEditPage.setQuestDescription("Edited Quest Description");
        questEditPage.clickSaveQuestButton();
        currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/quest-edit"));
        assertEquals("Edited Quest Name", questEditPage.getQuestName());
        assertEquals("Edited Quest Description", questEditPage.getQuestDescription());
        questEditPage.clickToQuestsListButton();
        currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/profile"));

        ProfilePage profilePage = new ProfilePage(driver, port);
        List<WebElement> questCards = profilePage.getQuestCards();
        WebElement questCard = questCards.stream().filter(c -> c.findElement(By.className("quest-name")).getText().equals("Edited Quest Name")).findFirst().orElse(null);
        assertNotNull(questCard);
        WebElement deleteButton = questCard.findElement(By.cssSelector(".btn-danger"));
        deleteButton.click();
        driver.switchTo().alert().accept();
        List<WebElement> remainingQuests = driver.findElements(By.xpath("//div[@class='card']//h5[contains(text(), 'Edited Quest Name')]"));
        assertTrue(remainingQuests.isEmpty());
    }

    @Test
//    @Order(4)
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
        questNameField.sendKeys("Q");
        questDescriptionField.clear();
        questDescriptionField.sendKeys("Valid Description");
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
        assertEquals("Quest name must be between 3 and 100 characters", errorAlert.getText());

        questNameField = driver.findElement(By.id("questName"));
        questDescriptionField = driver.findElement(By.id("questDescription"));
        questTextArea = driver.findElement(By.id("exampleFormControlTextarea1"));
        createButton = driver.findElement(By.id("submit"));
        questNameField.clear();
        questNameField.sendKeys("Valid Quest Name");
        questDescriptionField.clear();
        questDescriptionField.sendKeys("D");
        questTextArea.clear();
        questTextArea.sendKeys(modalText);
        createButton.click();
        errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        assertEquals("Quest description must be between 10 and 200 characters", errorAlert.getText());

        questNameField = driver.findElement(By.id("questName"));
        questDescriptionField = driver.findElement(By.id("questDescription"));
        questTextArea = driver.findElement(By.id("exampleFormControlTextarea1"));
        createButton = driver.findElement(By.id("submit"));
        questNameField.clear();
        questNameField.sendKeys("Valid Quest Name");
        questDescriptionField.clear();
        questDescriptionField.sendKeys("Valid Quest Description");
        questTextArea.clear();
        questTextArea.sendKeys("Invalid Content");
        createButton.click();
        errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        assertEquals("The quest text is not valid.", errorAlert.getText());
    }
}
