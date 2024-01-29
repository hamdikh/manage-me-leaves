package com.groupehillstone.aspect.logging;

import com.google.common.base.Stopwatch;
import com.groupehillstone.ManageMeLeavesApplication;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RepositoryLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(ManageMeLeavesApplication.class);

    @Pointcut("this(org.springframework.data.repository.Repository)")
    public void repositoryPointCut() {
        // pointcut definition
    }

    @Around("repositoryPointCut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        final var methodInvocationProceedingJoinPoint = (MethodInvocationProceedingJoinPoint) joinPoint;
        final String repositoryName = getRepositoryName(methodInvocationProceedingJoinPoint);
        final String methodName = getMethodName(methodInvocationProceedingJoinPoint);
        logger.info("BEGIN REPOSITORY CALL to {}#{}", repositoryName, methodName);
        Stopwatch watcher = Stopwatch.createStarted();
        try {
            return joinPoint.proceed();
        } finally {
            long time = watcher.elapsed(TimeUnit.MILLISECONDS);
            logger.info("FINISHED REPOSITORY CALL to {}#{}; duration: {} ms", repositoryName, methodName, time);
        }
    }

    private String getRepositoryName(MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint) {
        return methodInvocationProceedingJoinPoint.getSignature().getDeclaringTypeName();
    }

    private String getMethodName(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }
}
