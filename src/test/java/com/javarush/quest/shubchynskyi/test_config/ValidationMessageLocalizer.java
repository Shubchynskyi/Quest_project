package com.javarush.quest.shubchynskyi.test_config;

import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationMessageLocalizer {
    public static String getValidationFieldMessage(String key) {
        if (key.startsWith("{") && key.endsWith("}")) {
            key = key.substring(1, key.length() - 1);
        }
        return ErrorLocalizer.getLocalizedMessage(key);
    }
}
