package com.javarush.quest.shubchynskyi.aspects;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GlobalExceptionAspectTest {

    @Test
    void testLogAfterThrowingAllMethods() {
        // Инициализируем аспект
        GlobalExceptionAspect aspect = new GlobalExceptionAspect();

        // Создаем LogCaptor для класса GlobalExceptionAspect
        LogCaptor logCaptor = LogCaptor.forClass(GlobalExceptionAspect.class);

        // Создаем тестовое исключение
        Exception ex = new Exception("Test exception");

        // Вызываем метод аспекта напрямую
        aspect.logAfterThrowingAllMethods(ex);

        // Проверяем, что был записан лог уровня ERROR
        Assertions.assertFalse(logCaptor.getErrorLogs().isEmpty(), "Должен быть хотя бы один лог уровня ERROR");

        // Получаем записанный лог
        String logMessage = logCaptor.getErrorLogs().get(0);

        // Проверяем содержимое лога
        Assertions.assertTrue(logMessage.contains("Exception caught in class"),
                "Сообщение лога должно содержать 'Exception caught in class'");
    }
}