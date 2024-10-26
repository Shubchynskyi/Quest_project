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
        // Получаем TestLogger для класса GlobalExceptionAspect
        TestLogger testLogger = TestLoggerFactory.getTestLogger(GlobalExceptionAspect.class);

        // Вызываем метод, который выбрасывает исключение
        try {
            testExceptionService.methodThatThrowsException();
        } catch (Exception e) {
            // Ожидаемое исключение
        }
        //TODO refactoring
        // Проверяем, что лог-сообщение было записано
        Assertions.assertFalse(testLogger.getAllLoggingEvents().isEmpty(), "Must be one log minimum");

        // Получаем записанное сообщение
        String logMessage = testLogger.getLoggingEvents().getFirst().getMessage();

        // Проверяем содержимое сообщения
        Assertions.assertTrue(logMessage.contains("Exception caught in class"),
                "Must be 'Exception caught in class'");

        // Очищаем логгер после теста
        TestLoggerFactory.clear();
    }
}
