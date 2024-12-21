package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.INDEX_URL;

public class IndexPage extends BasePage {

    public IndexPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void openIndexPage() {
        driver.get(getBaseUrl() + INDEX_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("header")));
    }

    public WebElement getHeader() {
        return driver.findElement(By.tagName("header"));
    }

    public List<WebElement> getHeaderLinks() {
        return getHeader().findElements(By.tagName("a"));
    }

    public void clickLink(String text) {
        getHeaderLinks().stream().filter(l -> l.getText().contains(text)).findFirst().ifPresent(WebElement::click);
    }

    public void changeLanguage(String lang) {
        WebElement flag = driver.findElement(By.xpath("//a[@onclick=\"changeLanguage('" + lang + "');\"]"));
        flag.click();
    }

    public WebElement getFooter() {
        return driver.findElement(By.tagName("footer"));
    }

    public boolean isOnIndexPage() {
        wait.until(ExpectedConditions.urlContains(INDEX_URL));
        return getCurrentUrl().contains(INDEX_URL);
    }

    public WebElement getLoginLink() {
        return getHeaderLinks().stream()
                .filter(link -> link.getText().equalsIgnoreCase("Login"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Login link not found in header"));
    }

}