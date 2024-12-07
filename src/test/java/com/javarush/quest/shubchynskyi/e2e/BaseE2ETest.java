package com.javarush.quest.shubchynskyi.e2e;

import com.javarush.quest.shubchynskyi.App;
import com.javarush.quest.shubchynskyi.test_config.PostgresContainerConfiguration;
import com.javarush.quest.shubchynskyi.test_config.TestLocaleConfiguration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;


@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {App.class, PostgresContainerConfiguration.class, TestLocaleConfiguration.class}
)
public abstract class BaseE2ETest {

    protected static WebDriver driver;

    @Value("${local.server.port}")
    protected int port;

    @BeforeAll
    public static void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
//        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--lang=en-US");

        driver = new ChromeDriver(options);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}