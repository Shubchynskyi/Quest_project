package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.ProfilePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProfileE2ETest extends BaseE2ETest {

    private ProfilePage profilePage;

    @BeforeEach
    public void init() {
        profilePage = new ProfilePage(driver, port);
    }

    @Test
    @DisplayName("Should redirect to login when accessing profile without authentication")
    public void shouldRedirectToLoginWhenAccessingProfileWithoutAuthentication() {
        driver.get(getBaseUrl() + PROFILE_URL);
        assertTrue(driver.getCurrentUrl().contains(LOGIN_URL));
    }

    @Test
    @DisplayName("Should display profile for admin")
    public void shouldDisplayProfileForAdmin() {
        loginAsAdmin();
        profilePage.open();
        assertTrue(profilePage.isOnProfilePage());
        assertTrue(profilePage.getLoginText().contains(adminLogin));
        assertTrue(profilePage.getRoleText().contains(adminRole));
    }

    @Test
    @DisplayName("Should redirect to edit user page")
    public void shouldRedirectToEditUserPage() {
        loginAsAdmin();
        profilePage.open();
        profilePage.clickEditUserButton();
        assertTrue(driver.getCurrentUrl().contains(USER_URL));
    }

    @Test
    @DisplayName("Should display quests for admin")
    public void shouldDisplayQuestsForAdmin() {
        loginAsAdmin();
        profilePage.open();
        List<WebElement> questCards = profilePage.getQuestCards();
        assertFalse(questCards.isEmpty());
        for (WebElement card : questCards) {
            WebElement questName = card.findElement(org.openqa.selenium.By.className("quest-name"));
            WebElement questDescription = card.findElement(org.openqa.selenium.By.className("quest-description"));
            assertNotNull(questName);
            assertNotNull(questDescription);
        }
    }

    @Test
    @DisplayName("Should redirect to edit quest page")
    public void shouldRedirectToEditQuestPage() {
        loginAsAdmin();
        profilePage.open();
        List<WebElement> questCards = profilePage.getQuestCards();
        if (questCards.isEmpty()) fail("No quests available to edit.");
        profilePage.clickEditQuestButtonForFirstQuest();
        assertTrue(driver.getCurrentUrl().contains(QUEST_EDIT_URL));
    }

    @Test
    @DisplayName("Should show delete confirmation for quest")
    public void shouldShowDeleteConfirmationForQuest() {
        loginAsAdmin();
        profilePage.open();
        List<WebElement> questCards = profilePage.getQuestCards();
        if (questCards.isEmpty()) fail("No quests available to delete.");
        profilePage.clickDeleteQuestButtonForFirstQuest();
        assertTrue(profilePage.isDeleteConfirmationDisplayed());
        Alert alert = profilePage.getDeleteConfirmationAlert();
        assertNotNull(alert);
        assertTrue(alert.getText().contains("Are you sure"));
        alert.dismiss();
    }
}