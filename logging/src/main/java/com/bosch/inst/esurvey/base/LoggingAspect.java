package com.bosch.inst.esurvey.base;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class LoggingAspect {


    @Before("@annotation(org.springframework.web.bind.annotation.RequestMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void request(JoinPoint point) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String log = args2String(point);

        logger(point).info("{} {}: ({})", request.getMethod(), request.getRequestURI(), log);
    }

    @Before("@annotation(org.springframework.context.event.EventListener)")
    public void logEvents(JoinPoint point) {
        log(point);
    }

    @Before("@within(org.springframework.stereotype.Service)")
    public void logMethod(JoinPoint point) {
        log(point);
    }

    private Logger logger(JoinPoint point) {
        return LoggerFactory.getLogger(point.getSignature().getDeclaringType().getSimpleName());
    }

    private String method(JoinPoint point) {
        return point.getSignature().getName();
    }

    private void log(JoinPoint point) {
        String method = method(point);
        String args = args2String(point);

        logger(point).info("{}: ({})", method, args);
    }

    private String args2String(JoinPoint point) {
        Object[] args = point.getArgs();

        if (args == null || args.length == 0) {
            return "''";
        } else {
            return args2String(point, args);
        }
    }

    @SuppressWarnings("squid:S135") //increased number of continue statements in a loop to two instead of one
    private String args2String(JoinPoint point, Object[] args) {

        List<String> list = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg == null || arg.toString().isEmpty()) {
                list.add("null");
                continue;
            }

            if (exludeLogging(point, list, i)) {
                continue;
            }

            Class<?> argClass = arg.getClass();
            String parameter = argClass.getSimpleName();
            String packageString = argClass.getPackage().toString();

            if (packageString.contains("com.bosch.inst.esurvey.base.querydsl")) {
                continue;
            }

            if (packageString.contains("com.bosch.inst.esurvey")) {
                parameter = arg.toString();
            }

            if (packageString.contains("java.lang") ||
                    packageString.contains("java.time")) {
                parameter = String.format("%s: '%s'", argClass.getSimpleName(), arg.toString());
            }

            list.add(String.format("%s", parameter));
        }

        return String.join(", ", list);
    }

    private boolean exludeLogging(JoinPoint point, List<String> list, int i) {

        MethodSignature signature = point.getSignature() instanceof MethodSignature ? ((MethodSignature) point.getSignature()) : null;
        if (signature != null && signature.getMethod() != null) {
            for (Annotation annotation : signature.getMethod().getParameterAnnotations()[i]) {
                if (annotation instanceof ExcludeLogging) {
                    String parameter = "***";
                    list.add(String.format("%s", parameter));
                    return true;
                }
            }
        }
        return false;
    }

}