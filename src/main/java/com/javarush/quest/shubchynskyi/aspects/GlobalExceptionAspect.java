package com.javarush.quest.shubchynskyi.aspects;

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
        log.error("Exception caught in class [{}] in method [{}]: {}",
                ex.getStackTrace()[0].getClassName(),
                ex.getStackTrace()[0].getMethodName(),
                ex.getMessage(),
                ex);
    }
}