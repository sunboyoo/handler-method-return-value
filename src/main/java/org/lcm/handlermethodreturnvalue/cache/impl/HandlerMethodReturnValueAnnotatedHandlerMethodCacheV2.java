package org.lcm.handlermethodreturnvalue.cache.impl;

import org.lcm.handlermethodreturnvalue.annotation.HandlerMethodReturnValue;
import org.lcm.handlermethodreturnvalue.cache.HandlerMethodReturnValueAnnotatedHandlerMethodCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 做一个缓存，优化。
 * 启动的时候扫描所有带注解 @HandlerMethodReturnValue + @ResponseBody 的 Controller方法
 * 储存在 List 中，使用的时候不需要再使用反射
 *
 * 实现方式
 *  (1) WebApplicationContext by @Autowired
 *  (2) InitializingBean
 *
 *  优点：
 *   (1) 在 AbstractController 上面加注解 @HandlerMethodReturnValue + @ResponseBody, 可以获取到。
 */
public class HandlerMethodReturnValueAnnotatedHandlerMethodCacheV2 implements HandlerMethodReturnValueAnnotatedHandlerMethodCache, InitializingBean {
    // WebApplicationContext 可以获取 RequestMappingHandlerMapping
    private final WebApplicationContext webApplicationContext;
    private final List<Method> annotatedHandlerMethods = new CopyOnWriteArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(HandlerMethodReturnValueAnnotatedHandlerMethodCacheV2.class);

    public HandlerMethodReturnValueAnnotatedHandlerMethodCacheV2(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RequestMappingHandlerMapping mapping = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
        handlerMethods.forEach((info, handlerMethod) -> {
            // (1) 方法上有 @ResponseBody, 或者类上有 @ResponseBody 或者 @RestController
            // (2) 方法上有 @HandlerMethodReturnValue, 或者类上有 @ResponseResult
            Method method = handlerMethod.getMethod();
            Class<?> type = method.getDeclaringClass();
            if ((method.isAnnotationPresent(ResponseBody.class) ||
                    type.isAnnotationPresent(ResponseBody.class) ||
                    type.isAnnotationPresent(RestController.class)) &&
                    (method.isAnnotationPresent(HandlerMethodReturnValue.class) ||
                            type.isAnnotationPresent(HandlerMethodReturnValue.class))) {
                // 对于缓存池的写入，双检查+加锁
                synchronized (this.annotatedHandlerMethods) {
                    annotatedHandlerMethods.add(method);
                }
            }
        });
        logger.info("Controller Handler Methods with annotation @HandlerMethodReturnValue and @ResponseBody");
        logger.info(">>> Total: " + annotatedHandlerMethods.size());
        for (int i = 0; i < annotatedHandlerMethods.size(); i++){
            logger.info(String.format("%4d: ", i) + annotatedHandlerMethods.get(i).toString());
        }
    }


    public boolean contains(Method method){
        // 此处注意查看源码
        // Method.equals()
        // CopyOnWriteArrayList.contains()
        return annotatedHandlerMethods.contains(method);
    }

    private void containsTest() throws NoSuchMethodException {
//        System.out.println("两个不同的Method对象，使用equals方法比较时，比较的是其反射的原始类名和方法名。");
//        Method method01 = HelloController.class.getMethod("test01");
//        Method method02 = HelloController.class.getMethod("test01");
//        System.out.println("test02 == test01 -> " + (method01 == method02));
//        System.out.println("包含test01:" + contains(method01));
//        System.out.println("包含test02:" + contains(method02));
    }
}
