package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.App;
import com.javarush.quest.shubchynskyi.test_config.PostgresContainerConfiguration;
import com.javarush.quest.shubchynskyi.test_config.TestLocaleConfiguration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {App.class, PostgresContainerConfiguration.class, TestLocaleConfiguration.class}
)
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class BaseE2ETest {

    protected WebDriver driver;

    @Value("${local.server.port}")
    protected int port;

    @Value("${e2e.admin.login}")
    protected String adminLogin;

    @Value("${e2e.admin.password}")
    protected String adminPassword;

    @Value("${e2e.moderator.login}")
    protected String moderatorLogin;

    @Value("${e2e.moderator.password}")
    protected String moderatorPassword;

    @Value("${e2e.user.login}")
    protected String userLogin;

    @Value("${e2e.user.password}")
    protected String userPassword;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--lang=en-US");
        options.addArguments("--headless=new"); //todo
        driver = new ChromeDriver(options);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    protected void loginAs(String login, String password) {
        driver.get(getBaseUrl() + "/login");
        WebElement loginField = driver.findElement(By.id("userLogin"));
        WebElement passwordField = driver.findElement(By.id("userPassword"));
        WebElement submitButton = driver.findElement(By.id("submit"));
        loginField.clear();
        loginField.sendKeys(login);
        passwordField.clear();
        passwordField.sendKeys(password);
        submitButton.click();
    }

    protected void loginAsAdmin() {
        loginAs(adminLogin, adminPassword);
    }

    protected void loginAsModerator() {
        loginAs(moderatorLogin, moderatorPassword);
    }

    protected void loginAsUser() {
        loginAs(userLogin, userPassword);
    }
}