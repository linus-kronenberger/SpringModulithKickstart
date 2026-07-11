package com.example.springmodulithkickstart.shared.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant

@Aspect
@Component
class ControllerLoggingAspect {

    private val log = LoggerFactory.getLogger(ControllerLoggingAspect::class.java)

    @Around("execution(* com.example.springmodulithkickstart..*Controller.*(..))")
    fun logControllerMethods(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = joinPoint.signature.name
        val className = joinPoint.target.javaClass.simpleName
        val start = Instant.now()
        log.info("[{}.{}] Start at: {}", className, methodName, start)
        return try {
            val result = joinPoint.proceed()
            val end = Instant.now()
            log.info("[{}.{}] End at: {}", className, methodName, end)
            result
        } catch (e: Throwable) {
            val end = Instant.now()
            log.error("[{}.{}] Exception at: {}", className, methodName, end, e)
            throw e
        }
    }
}
