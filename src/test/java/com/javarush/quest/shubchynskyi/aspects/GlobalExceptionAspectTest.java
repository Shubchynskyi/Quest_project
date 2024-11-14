package com.javarush.quest.shubchynskyi.aspects;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.javarush.quest.shubchynskyi.TestConstants.*;


class GlobalExceptionAspectTest {

    @Test
    void testLogAfterThrowingAllMethods() {
        TestLogger testLogger = TestLoggerFactory.getTestLogger(GlobalExceptionAspect.class);

        GlobalExceptionAspect aspect = new GlobalExceptionAspect();

        Exception ex = new Exception(TEST_EXCEPTION);

        aspect.logAfterThrowingAllMethods(ex);

        Assertions.assertFalse(testLogger.getAllLoggingEvents().isEmpty(), MUST_BE_ONE_LOG_MINIMUM);

        String logMessage = testLogger.getLoggingEvents().getFirst().getMessage();

        Assertions.assertTrue(logMessage.contains(EXCEPTION_CAUGHT_IN_CLASS),
                MUST_BE_EXCEPTION_CAUGHT_IN_CLASS);

        TestLoggerFactory.clear();
    }
}