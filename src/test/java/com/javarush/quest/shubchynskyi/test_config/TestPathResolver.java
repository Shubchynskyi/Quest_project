package com.javarush.quest.shubchynskyi.test_config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestPathResolver {

    public static String resolvePath(String route) {
        return "/" + route;
    }
}
