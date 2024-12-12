package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.LayoutPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LayoutE2ETest extends BaseE2ETest {

    @Test
//    @Order(1)
    @DisplayName("Should redirect to login when accessing create quest without authentication")
    void shouldRedirectToLoginWhenAccessingCreateQuestWithoutAuthentication() {
        driver.get(getBaseUrl() + "/create-quest");
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
//    @Order(2)
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
//    @Order(3)
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
//    @Order(4)
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
//    @Order(5)
    @DisplayName("Should switch language when flag clicked")
    void shouldSwitchLanguageWhenFlagClicked() {
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        layoutPage.changeLanguage("ru");
        assertTrue(driver.getCurrentUrl().contains("lang=ru"));
    }

    @Test
//    @Order(6)
    @DisplayName("Should display footer with copyright")
    void shouldDisplayFooterWithCopyright() {
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        WebElement footer = layoutPage.getFooter();
        assertNotNull(footer);
        assertTrue(footer.getText().contains("© 2023-2025 Shubchynskyi"));
    }

    @Test
//    @Order(7)
    @DisplayName("Should navigate to correct pages from header")
    void shouldNavigateToCorrectPagesFromHeader() {
        loginAsUser();
        LayoutPage layoutPage = new LayoutPage(driver, port);
        layoutPage.openMainPage();
        layoutPage.clickLink("Profile");
        assertTrue(driver.getCurrentUrl().contains("/profile"));
    }
}