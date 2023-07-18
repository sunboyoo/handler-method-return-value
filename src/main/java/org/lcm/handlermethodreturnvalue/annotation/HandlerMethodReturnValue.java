package org.lcm.handlermethodreturnvalue.annotation;

import java.lang.annotation.*;

/**
 * 注解 @HandlerMethodReturnValue 用来标记 Controller 方法的返回值，是否需要包装为 ReturnValue 类型
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HandlerMethodReturnValue {
}
