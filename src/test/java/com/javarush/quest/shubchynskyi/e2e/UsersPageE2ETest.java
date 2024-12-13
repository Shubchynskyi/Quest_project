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

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
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

        usersPage.waitForPageToLoad();
        List<WebElement> userCards = usersPage.getUserCards();
        assertFalse(userCards.isEmpty(), "No user cards found on the page.");

        for (WebElement userCard : userCards) {
            // Click edit button
            usersPage.clickEditButton(userCard);
            assertTrue(driver.getCurrentUrl().contains(USER_URL), "Edit button did not redirect to /user.");

            // Navigate back and wait for reload
            driver.navigate().back();
            usersPage.waitForPageToLoad();

            // Click delete button
            usersPage.clickDeleteButton(userCard);

            // Confirm alert
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.alertIsPresent());
            assertNotNull(alert, "Delete confirmation alert not displayed.");
            alert.dismiss();

            // Динамическое ожидание исчезновения алерта
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.not(ExpectedConditions.alertIsPresent()));

            // Дополнительное ожидание стабилизации DOM (если требуется)
            usersPage.waitForPageToLoad(); // Дождаться, что DOM вернулся в стабильное состояние
        }
    }



    @Test
//    @Order(2)
    @DisplayName("Unauthorized user should not have access to users page")
    void unauthorizedUserShouldNotHaveAccessToUsersPage() {
        loginAsUser();
        driver.get(getBaseUrl() + USERS_URL);
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(INDEX_URL));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'alert-danger')]")
        ));
        assertNotNull(errorMessage);
        assertEquals("You don't have permissions", errorMessage.getText().trim());
    }
}
