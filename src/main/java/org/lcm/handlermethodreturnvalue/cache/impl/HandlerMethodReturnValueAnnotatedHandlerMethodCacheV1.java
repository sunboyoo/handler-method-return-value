package org.lcm.handlermethodreturnvalue.cache.impl;

import org.lcm.handlermethodreturnvalue.annotation.HandlerMethodReturnValue;
import org.lcm.handlermethodreturnvalue.cache.HandlerMethodReturnValueAnnotatedHandlerMethodCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * 做一个缓存，优化。
 * 启动的时候扫描所有带注解 @HandlerMethodReturnValue + @ResponseBody 的 Controller方法
 * 储存在 List 中，使用的时候不需要再使用反射
 *
 * 实现方式
 *  (1) ApplicationContextAware
 *  (2) InitializingBean
 *
 *  缺点：
 *   (1) 在 AbstractController 上面加注解 @HandlerMethodReturnValue + @ResponseBody, 无法被获取到。
 */
public class HandlerMethodReturnValueAnnotatedHandlerMethodCacheV1 implements HandlerMethodReturnValueAnnotatedHandlerMethodCache, ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    private final List<Method> annotatedHandlerMethods = new CopyOnWriteArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(HandlerMethodReturnValueAnnotatedHandlerMethodCacheV1.class);

    @Override
    public void afterPropertiesSet() throws Exception{
        Map<String, Object> controllers = this.applicationContext.getBeansWithAnnotation(Controller.class);

        controllers.forEach((key, controller) -> {
            Class<?> controllerClass = controller.getClass();

            boolean hasClassAnnotationRestController = controllerClass.isAnnotationPresent(RestController.class);
            boolean hasClassAnnotationResponseBody = controllerClass.isAnnotationPresent(ResponseBody.class);
            boolean hasClassAnnotationResponseResult = controllerClass.isAnnotationPresent(HandlerMethodReturnValue.class);

            Method[] methods = controllerClass.getDeclaredMethods();

            // 对于缓存池的写入，双检查+加锁
            synchronized (this.annotatedHandlerMethods) {
                Stream.of(methods)
                        .filter(method -> {
                            boolean hasMethodAnnotationResponseBody = method.isAnnotationPresent(ResponseBody.class);
                            boolean hasMethodAnnotationResponseResult = method.isAnnotationPresent(HandlerMethodReturnValue.class);
                            return (hasClassAnnotationRestController || hasClassAnnotationResponseBody || hasMethodAnnotationResponseBody) &&
                                    (hasClassAnnotationResponseResult || hasMethodAnnotationResponseResult);
                        })
                        .forEach(annotatedHandlerMethods::add);
            }
        });

        logger.info("Controller Handler Methods with annotation @HandlerMethodReturnValue and @ResponseBody");
        logger.info(">>> Total: " + annotatedHandlerMethods.size());
        for (int i = 0; i < annotatedHandlerMethods.size(); i++){
            logger.info(String.format("%4d: ", i) + annotatedHandlerMethods.get(i).toString());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public boolean contains(Method method){
        // 此处注意查看源码
        // Method.equals() 两个不同的Method对象，使用equals方法比较时，比较的是其反射的原始类名和方法名。
        // CopyOnWriteArrayList.contains()
        return annotatedHandlerMethods.contains(method);
    }
}
