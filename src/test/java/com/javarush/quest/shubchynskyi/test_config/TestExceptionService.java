package com.javarush.quest.shubchynskyi.test_config;

import org.springframework.stereotype.Service;

@Service
public class TestExceptionService {

    public void methodThatThrowsException() {
        throw new RuntimeException(TestConstants.TEST_EXCEPTION);
    }
}
