package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.e2e.pageobjects.UsersPage;
import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.YOU_DONT_HAVE_PERMISSIONS;
import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

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

        testEditFunctionality(usersPage);
        testDeleteFunctionality(usersPage);
    }

    public void testEditFunctionality(UsersPage usersPage) {
        WebElement editButton = usersPage.getFirstEditButton();
        assertNotNull(editButton, "Edit button not found.");
        editButton.click();
        assertTrue(driver.getCurrentUrl().contains(USER_URL), "Edit button did not redirect to /user.");
        driver.navigate().back();
        usersPage.waitForPageToLoad();
        assertTrue(usersPage.isOnUsersPage(), "Failed to navigate back to the Users page.");
    }

    public void testDeleteFunctionality(UsersPage usersPage) {
        WebElement deleteButton = usersPage.getFirstDeleteButton();
        assertNotNull(deleteButton, "Delete button not found.");
        deleteButton.click();
        Alert alert = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.alertIsPresent());
        assertNotNull(alert, "Delete confirmation alert not displayed.");
        alert.dismiss();
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.not(ExpectedConditions.alertIsPresent()));
        usersPage.waitForPageToLoad();
        assertTrue(usersPage.isOnUsersPage(), "Users page did not stabilize after dismissing delete alert.");
    }

    @Test
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
        assertEquals(ErrorLocalizer.getLocalizedMessage(YOU_DONT_HAVE_PERMISSIONS),
                errorMessage.getText().trim());
    }
}