package com.javarush.quest.shubchynskyi.config.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Component
@Aspect
@EnableAspectJAutoProxy
public class LoggerAspectPointcut {

    @Pointcut("@annotation(com.javarush.quest.shubchynskyi.config.aspects.LoggerAspect)")
    public void isMethodAnnotation(){
    }
}
