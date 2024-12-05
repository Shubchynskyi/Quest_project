package com.javarush.quest.shubchynskyi.unit.aspects;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.javarush.quest.shubchynskyi.aspects.GlobalExceptionAspect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.javarush.quest.shubchynskyi.test_config.TestConstants.*;


class GlobalExceptionAspectTest {

    @Test
    void testLogAfterThrowingAllMethods() {
        TestLogger testLogger = TestLoggerFactory.getTestLogger(GlobalExceptionAspect.class);

        GlobalExceptionAspect aspect = new GlobalExceptionAspect();

        Exception ex = new Exception(TEST_EXCEPTION);

        aspect.logAfterThrowingAllMethods(ex);

        Assertions.assertFalse(testLogger.getAllLoggingEvents().isEmpty(), ASSERT_MINIMUM_ONE_LOG);

        String logMessage = testLogger.getLoggingEvents().getFirst().getMessage();

        Assertions.assertTrue(logMessage.contains(LOG_EXCEPTION_CAUGHT_IN_CLASS),
                ASSERT_EXCEPTION_CAUGHT_IN_CLASS);

        TestLoggerFactory.clear();
    }
}