package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.USERS_URL;

public class UsersPage extends BasePage {

    public UsersPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void open() {
        driver.get(getBaseUrl() + USERS_URL);
        waitForPageToLoad();
    }

    public WebElement getFirstEditButton() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.btn-primary")));
    }

    public WebElement getFirstDeleteButton() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.btn-danger")));
    }

    public void waitForPageToLoad() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        wait.until(driver -> js.executeScript("return document.readyState").equals("complete"));
    }
}