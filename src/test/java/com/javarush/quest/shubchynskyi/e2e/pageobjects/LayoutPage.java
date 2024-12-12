package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class LayoutPage extends BasePage {

    public LayoutPage(WebDriver driver, int port) {
        super(driver, port);
    }

    public void openMainPage() {
        driver.get(getBaseUrl() + "/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("header")));
    }

    public WebElement getHeader() {
        return driver.findElement(By.tagName("header"));
    }

    public List<WebElement> getHeaderLinks() {
        return getHeader().findElements(By.tagName("a"));
    }

    public boolean hasLink(String text) {
        return getHeaderLinks().stream().anyMatch(link -> link.getText().contains(text));
    }

    public void clickLink(String text) {
        WebElement link = getHeaderLinks().stream().filter(l -> l.getText().contains(text)).findFirst().orElse(null);
        if (link != null) link.click();
    }

    public void changeLanguage(String lang) {
        WebElement flag = driver.findElement(By.xpath("//a[@onclick=\"changeLanguage('" + lang + "');\"]"));
        flag.click();
    }

    public WebElement getFooter() {
        return driver.findElement(By.tagName("footer"));
    }
}