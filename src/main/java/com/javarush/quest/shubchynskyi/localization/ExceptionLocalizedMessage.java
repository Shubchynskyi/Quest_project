package com.javarush.quest.shubchynskyi.localization;


public record ExceptionLocalizedMessage(
        String message,
        boolean isMessageService
) {}