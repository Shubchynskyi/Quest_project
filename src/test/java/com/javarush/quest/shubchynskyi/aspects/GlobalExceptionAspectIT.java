package com.javarush.quest.shubchynskyi.aspects;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import com.javarush.quest.shubchynskyi.test_config.TestExceptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GlobalExceptionAspectIT {

    @Autowired
    private TestExceptionService testExceptionService;

    @Test
    void testExceptionLoggingAspect() {
        TestLogger testLogger = TestLoggerFactory.getTestLogger(GlobalExceptionAspect.class);
        try {
            testExceptionService.methodThatThrowsException();
        }
        catch (Exception e) {
            // Exception is expected, verifying logs
        }

        Assertions.assertFalse(testLogger.getAllLoggingEvents().isEmpty(), "Must be one log minimum");

        String logMessage = testLogger.getLoggingEvents().getFirst().getMessage();

        Assertions.assertTrue(logMessage.contains("Exception caught in class"),
                "Must be 'Exception caught in class'");

        TestLoggerFactory.clear();
    }
}