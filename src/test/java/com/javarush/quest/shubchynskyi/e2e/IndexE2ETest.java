package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.IndexPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class IndexE2ETest extends BaseE2ETest {

    @Value("${e2e.languageToChange}")
    private String languageToChange;

    @Test
    @DisplayName("Should redirect to login when accessing create quest without authentication")
    void shouldRedirectToLoginWhenAccessingCreateQuestWithoutAuthentication() {
        driver.get(getBaseUrl() + CREATE_QUEST_URL);
        assertTrue(driver.getCurrentUrl().contains(LOGIN_URL));
    }

    @Test
    @DisplayName("Should display profile and play links for user")
    void shouldDisplayProfileAndPlayLinksForUser() {
        loginAsUser();
        IndexPage indexPage = new IndexPage(driver, port);
        indexPage.openMainPage();
        List<WebElement> links = indexPage.getHeaderLinks();
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Profile")));
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Play")));
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Create Quest")));
        assertFalse(links.stream().anyMatch(link -> link.getText().contains("Users")));
    }

    @Test
    @DisplayName("Should display users link for moderator")
    void shouldDisplayCreateQuestLinkForModerator() {
        loginAsModerator();
        IndexPage indexPage = new IndexPage(driver, port);
        indexPage.openMainPage();
        List<WebElement> links = indexPage.getHeaderLinks();
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Users")));
    }

    @Test
    @DisplayName("Should display users link for admin")
    void shouldDisplayCreateQuestLinkForAdmin() {
        loginAsAdmin();
        IndexPage indexPage = new IndexPage(driver, port);
        indexPage.openMainPage();
        List<WebElement> links = indexPage.getHeaderLinks();
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Users")));
    }

    @Test
    @DisplayName("Should switch language when flag clicked")
    void shouldSwitchLanguageWhenFlagClicked() {
        IndexPage indexPage = new IndexPage(driver, port);
        indexPage.openMainPage();
        indexPage.changeLanguage(languageToChange);
        assertTrue(driver.getCurrentUrl().contains("lang=" + languageToChange));
    }

    @Test
    @DisplayName("Should display footer with copyright")
    void shouldDisplayFooterWithCopyright() {
        IndexPage indexPage = new IndexPage(driver, port);
        indexPage.openMainPage();
        WebElement footer = indexPage.getFooter();
        assertNotNull(footer);
        assertTrue(footer.getText().contains("©"));
    }

    @Test
    @DisplayName("Should navigate to correct pages from header")
    void shouldNavigateToCorrectPagesFromHeader() {
        loginAsUser();
        IndexPage indexPage = new IndexPage(driver, port);
        indexPage.openMainPage();
        indexPage.clickLink("Profile");
        assertTrue(driver.getCurrentUrl().contains(PROFILE_URL));
    }
}