package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.UsersPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersPageE2ETest extends BaseE2ETest {

    @ParameterizedTest
    @CsvSource({
            "admin, admin123",
            "moderator, moderator123"
    })
    @DisplayName("Should allow admin and moderator to manage users")
    public void shouldAllowAdminAndModeratorToManageUsers(String role, String password) {
        loginAs(role, password);

        UsersPage usersPage = new UsersPage(driver, port);
        usersPage.open();

        List<WebElement> userCards = usersPage.getUserCards();
        assertFalse(userCards.isEmpty(), "No user cards found on the page.");

        for (int i = 0; i < userCards.size(); i++) {
            // Refresh user cards before accessing each one
            userCards = usersPage.getUserCards();
            WebElement userCard = userCards.get(i);

            String userLogin = usersPage.getUserLogin(userCard);

//            // Skip admin user
//            if ("admin".equalsIgnoreCase(userLogin)) {
//                continue;
//            }

            // Test the "Edit" button
            usersPage.clickEditButton(userCard);
            assertTrue(driver.getCurrentUrl().contains("/user"), "Edit button did not redirect to /user.");
            driver.navigate().back();
            usersPage.waitForPageToLoad();

            // Refresh user cards again to avoid stale elements
            userCards = usersPage.getUserCards();
            userCard = userCards.get(i);

            // Test the "Delete" button
            usersPage.clickDeleteButton(userCard);

            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.alertIsPresent());
            assertNotNull(alert, "Delete confirmation alert not displayed.");
            alert.dismiss(); // Cancel the deletion
        }
    }

    @Test
    @Order(2)
    @DisplayName("Unauthorized user should not have access to users page")
    void unauthorizedUserShouldNotHaveAccessToUsersPage() {
        loginAsUser();
        driver.get(getBaseUrl() + "/users");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'alert-danger')]")
        ));
        assertNotNull(errorMessage);
        assertEquals("You don't have permissions", errorMessage.getText().trim());
    }
}
