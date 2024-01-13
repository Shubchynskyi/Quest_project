package com.javarush.quest.shubchynskyi.result;

public record UserDataProcessResult(
        String tempImageId,
        boolean hasFieldsErrors,
        boolean isTempImagePresent,
        boolean imageIsValid
) {}
