package com.javarush.quest.shubchynskyi.aspects;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class GlobalExceptionAspectTest {

    @Test
    void testLogAfterThrowingAllMethods() {
        // Получаем TestLogger для класса GlobalExceptionAspect
        TestLogger testLogger = TestLoggerFactory.getTestLogger(GlobalExceptionAspect.class);

        // Инициализируем аспект
        GlobalExceptionAspect aspect = new GlobalExceptionAspect();

        // Создаем тестовое исключение
        Exception ex = new Exception("Test exception");

        // Вызываем метод, который должен логировать исключение
        aspect.logAfterThrowingAllMethods(ex);

        // Проверяем, что лог-сообщение было записано
        Assertions.assertFalse(testLogger.getAllLoggingEvents().isEmpty(), "Должен быть хотя бы один лог");
//TODO refactoring
        // Получаем записанное сообщение
        String logMessage = testLogger.getLoggingEvents().getFirst().getMessage();

        // Проверяем содержимое сообщения
        Assertions.assertTrue(logMessage.contains("Exception caught in class"),
                "Сообщение лога должно содержать 'Exception caught in class'");

        // Очищаем логгер после теста
        TestLoggerFactory.clear();
    }
}
