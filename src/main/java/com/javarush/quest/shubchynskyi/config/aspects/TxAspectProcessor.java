package com.javarush.quest.shubchynskyi.config.aspects;

import com.javarush.quest.shubchynskyi.config.JavaApplicationConfig;
import com.javarush.quest.shubchynskyi.config.SessionCreator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class TxAspectProcessor {
    private final SessionCreator sessionCreator;

    @Before("TxAspectPointcut.isMethodAnnotation()")
    public void beforeMethodLoggerAnnotation(JoinPoint point) {
        sessionCreator.beginTransactional();
    }

    @After("TxAspectPointcut.isMethodAnnotation()")
    public void afterMethodLoggerAnnotation(JoinPoint point) {
        sessionCreator.endTransactional();
    }
}
