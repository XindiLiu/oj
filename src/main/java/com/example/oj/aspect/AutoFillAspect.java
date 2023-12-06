package com.example.oj.aspect;

import com.example.oj.annotation.AutoFill;
import com.example.oj.constant.AutofillConstants;
import com.example.oj.user.User;
import com.example.oj.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
//    @Pointcut("execution(* com.example.oj.*.*Repository.save(..)) && @annotation(com.example.oj.annotation.AutoFill)")
    @Pointcut("@annotation(com.example.oj.annotation.AutoFill)")
    public void fillPointcut(){}


    @Before("fillPointcut()")
    public void fill(JoinPoint joinPoint){
        log.info("Autofill");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null){
            return;
        }
        Object entity = args[0];
        var entityClass = entity.getClass();
        try {
            entityClass.getMethod(AutofillConstants.SET_CREATE_TIME);
        } catch (NoSuchMethodException e) {
            log.info("Nothing to autofill");
            return;
        }

        Long userId = BaseContext.getCurrentId();
        LocalDateTime currentTime =LocalDateTime.now();


        try {
            Long entityId = (Long) entityClass.getMethod(AutofillConstants.GET_ID).invoke(entity);

            if(entityClass.equals(User.class)){
                // It is an insert if the id is null, otherwise it is an update.
                if (entityId == null){
                    invokeMethod(entity, AutofillConstants.SET_CREATE_TIME, currentTime);
                    invokeMethod(entity, AutofillConstants.SET_UPDATE_TIME, currentTime);
                }
                else {
                    invokeMethod(entity, AutofillConstants.SET_UPDATE_TIME, currentTime);
                }
            }
            else{
                // It is an insert if the id is null, otherwise it is an update.
                if (entityId == null){
                    invokeMethod(entity, AutofillConstants.SET_CREATE_USER, userId);
                    invokeMethod(entity, AutofillConstants.SET_CREATE_TIME, currentTime);

                    invokeMethod(entity, AutofillConstants.SET_UPDATE_USER, userId);
                    invokeMethod(entity, AutofillConstants.SET_UPDATE_TIME, currentTime);

                }
                else {
                    invokeMethod(entity, AutofillConstants.SET_UPDATE_USER, userId);
                    invokeMethod(entity, AutofillConstants.SET_UPDATE_TIME, currentTime);
                }
            }
        } catch (Exception e){
            e.printStackTrace();

        }
    }
    private Object invokeMethod(Object obj, String method, Object... args){
        var objClass = obj.getClass();
        Class<?> parameterTypes[] = Arrays.stream(args).map((arg) -> arg.getClass()).toArray(Class<?>[]::new);
        try{
            var memberMethod = objClass.getMethod(method, parameterTypes);
            return memberMethod.invoke(obj, args);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
