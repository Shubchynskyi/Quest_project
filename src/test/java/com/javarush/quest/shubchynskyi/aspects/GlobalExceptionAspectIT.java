package com.javarush.quest.shubchynskyi.aspects;


import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import com.javarush.quest.shubchynskyi.test_config.TestExceptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@ConfigIT
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GlobalExceptionAspectIT {

//    @Autowired
//    private TestExceptionService testExceptionService;
//
//    @Test
//    void testExceptionLoggingAspect() {
//        // Создаем LogCaptor для класса GlobalExceptionAspect
//        LogCaptor logCaptor = LogCaptor.forClass(GlobalExceptionAspect.class);
//
//        // Вызываем метод сервиса, который выбрасывает исключение
//        try {
//            testExceptionService.methodThatThrowsException();
//        } catch (Exception e) {
//            // Ожидаемое исключение, ничего не делаем
//        }
//
//        // Проверяем, что был записан лог уровня ERROR
//        Assertions.assertFalse(logCaptor.getErrorLogs().isEmpty(), "Должен быть хотя бы один лог уровня ERROR");
//
//        // Получаем записанный лог
//        String logMessage = logCaptor.getErrorLogs().get(0);
//
//        // Проверяем содержимое лога
//        Assertions.assertTrue(logMessage.contains("Exception caught in class"),
//                "Сообщение лога должно содержать 'Exception caught in class'");
//    }
}
