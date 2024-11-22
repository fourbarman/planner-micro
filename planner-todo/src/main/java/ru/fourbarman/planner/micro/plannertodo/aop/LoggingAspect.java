package ru.fourbarman.planner.micro.plannertodo.aop;

import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/*
Логирование через аспект
Логирует все выполняемые методы контроллеров
 */
@Aspect
@Component
@Log
public class LoggingAspect {

    //аспект будет выполняться для всех методов из пакета контроллеров
    @Around("execution(* ru.fourbarman.planner.micro.plannertodo.controller..*(..))")
    public Object ProfileControllerMethods(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

        //получить инфо о том, какой класс и метод выполняются
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        log.info("---------- Executing " + className + "." + methodName + "() ----------");

        StopWatch stopWatch = new StopWatch();

        // выполняем сам метод например с замером времени
        stopWatch.start();
        Object result = pjp.proceed();
        stopWatch.stop();

        log.info("---------- Execution time of " + className + "." + methodName + "() :: " + stopWatch.getTotalTimeMillis() + "ms ----------");

        return result;
    }
}
