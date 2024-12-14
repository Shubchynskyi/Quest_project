package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.LayoutPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class LayoutE2ETest extends BaseE2ETest {

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
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        List<WebElement> links = layoutPage.getHeaderLinks();
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Profile")));
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Play")));
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Create Quest")));
        assertFalse(links.stream().anyMatch(link -> link.getText().contains("Users")));
    }

    @Test
    @DisplayName("Should display create quest link for moderator")
    void shouldDisplayCreateQuestLinkForModerator() {
        loginAsModerator();
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        List<WebElement> links = layoutPage.getHeaderLinks();
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Create Quest")));
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Users")));
    }

    @Test
    @DisplayName("Should display create quest link for admin")
    void shouldDisplayCreateQuestLinkForAdmin() {
        loginAsAdmin();
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        List<WebElement> links = layoutPage.getHeaderLinks();
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Create Quest")));
        assertTrue(links.stream().anyMatch(link -> link.getText().contains("Users")));
    }

    @Test
    @DisplayName("Should switch language when flag clicked")
    void shouldSwitchLanguageWhenFlagClicked() {
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        layoutPage.changeLanguage(languageToChange);
        assertTrue(driver.getCurrentUrl().contains("lang=" + languageToChange));
    }

    @Test
    @DisplayName("Should display footer with copyright")
    void shouldDisplayFooterWithCopyright() {
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        WebElement footer = layoutPage.getFooter();
        assertNotNull(footer);
        assertTrue(footer.getText().contains("©"));
    }

    @Test
    @DisplayName("Should navigate to correct pages from header")
    void shouldNavigateToCorrectPagesFromHeader() {
        loginAsUser();
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        layoutPage.clickLink("Profile");
        assertTrue(driver.getCurrentUrl().contains(PROFILE_URL));
    }
}