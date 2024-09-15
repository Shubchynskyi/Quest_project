package com.javarush.quest.shubchynskyi.aspects;

import com.javarush.quest.shubchynskyi.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class GlobalExceptionAspect {

    @AfterThrowing(pointcut = "execution(* com.javarush.quest.shubchynskyi..*(..))", throwing = "ex")
    public void logAfterThrowingAllMethods(Exception ex) {
        if (!(ex instanceof AppException)) {
            log.error("Exception caught by AOP: {}", ex.getMessage(), ex);
        }
    }
}
