package org.lcm.handlermethodreturnvalue.cache;

import java.lang.reflect.Method;

/*
 * 做一个缓存，优化。
 * 启动的时候扫描所有带注解 @HandlerMethodReturnValue + @ResponseBody 的 Controller方法
 * 储存在 List 中，使用的时候不需要再使用反射
 */
public interface HandlerMethodReturnValueAnnotatedHandlerMethodCache {
    boolean contains(Method method);
}
