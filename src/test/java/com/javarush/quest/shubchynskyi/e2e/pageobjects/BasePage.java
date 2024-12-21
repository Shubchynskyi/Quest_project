package com.javarush.quest.shubchynskyi.e2e.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final int port;

    public BasePage(WebDriver driver, int port) {
        this.driver = driver;
        this.port = port;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}