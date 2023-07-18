package org.lcm.handlermethodreturnvalue.cache.impl;

import org.lcm.handlermethodreturnvalue.annotation.HandlerMethodReturnValue;
import org.lcm.handlermethodreturnvalue.cache.HandlerMethodReturnValueAnnotatedHandlerMethodCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 做一个缓存，优化。
 * 启动的时候扫描所有带注解 @HandlerMethodReturnValue + @ResponseBody 的 Controller方法
 * 储存在 List 中，使用的时候不需要再使用反射
 *
 * 实现方式
 *  (1) InstantiationAwareBeanPostProcessor
 *
 *  缺点：
 *   (1) 获取到太多非 Controller 的method。
 *
 */
public class HandlerMethodReturnValueAnnotatedHandlerMethodCacheV4 implements HandlerMethodReturnValueAnnotatedHandlerMethodCache, InstantiationAwareBeanPostProcessor {

    private final List<Method> annotatedHandlerMethods = new CopyOnWriteArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(HandlerMethodReturnValueAnnotatedHandlerMethodCacheV4.class);

    public boolean contains(Method method){
        // 此处注意查看源码
        // Method.equals()
        // CopyOnWriteArrayList.contains()
        return annotatedHandlerMethods.contains(method);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> type = bean.getClass();
        // 判断是否为 Controller 类
        if (type.isAnnotationPresent(Controller.class) || type.isAnnotationPresent(RestController.class)) {
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                // (1) 方法上有 @ResponseBody, 或者类上有 @ResponseBody 或者 @RestController
                // (2) 方法上有 @HandlerMethodReturnValue, 或者类上有 @ResponseResult
                if ((method.isAnnotationPresent(ResponseBody.class) ||
                        type.isAnnotationPresent(ResponseBody.class) ||
                        type.isAnnotationPresent(RestController.class)) &&
                        (method.isAnnotationPresent(HandlerMethodReturnValue.class) ||
                                type.isAnnotationPresent(HandlerMethodReturnValue.class))) {
                    // 对于缓存池的写入，双检查+加锁
                    synchronized (this.annotatedHandlerMethods) {
                        annotatedHandlerMethods.add(method);
                        if (annotatedHandlerMethods.size() == 1){
                            logger.info("Controller Handler Methods with annotation @HandlerMethodReturnValue and @ResponseBody");
                        }
                        logger.info(String.format("%4d: ", annotatedHandlerMethods.size()) + method);
                    }
                }
            }
        }
        return null;
    }
}
