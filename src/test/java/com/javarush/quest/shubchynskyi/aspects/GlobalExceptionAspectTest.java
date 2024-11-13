package com.javarush.quest.shubchynskyi.aspects;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class GlobalExceptionAspectTest {

    @Test
    void testLogAfterThrowingAllMethods() {
        TestLogger testLogger = TestLoggerFactory.getTestLogger(GlobalExceptionAspect.class);

        GlobalExceptionAspect aspect = new GlobalExceptionAspect();

        Exception ex = new Exception("Test exception");

        aspect.logAfterThrowingAllMethods(ex);

        Assertions.assertFalse(testLogger.getAllLoggingEvents().isEmpty(), "Must be one log minimum");

        String logMessage = testLogger.getLoggingEvents().getFirst().getMessage();

        Assertions.assertTrue(logMessage.contains("Exception caught in class"),
                "Must be 'Exception caught in class'");

        TestLoggerFactory.clear();
    }
}