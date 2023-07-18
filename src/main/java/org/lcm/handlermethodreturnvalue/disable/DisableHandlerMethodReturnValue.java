package org.lcm.handlermethodreturnvalue.disable;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



/**
 * 检查 Request Header 中是否包含 HEADER_NAME 字段
 * 如果有，则不会再包装
 *
 * Request Header 可以配合 Spring Cloud Feign 传递信息
 */
public interface DisableHandlerMethodReturnValue {
    String HEADER_NAME = DisableHandlerMethodReturnValue.class.getName().replaceAll("\\.", "-");

    static boolean existsInHttpRequestHeader(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            return request.getHeader(HEADER_NAME) != null;
        }
        return false;
    }
}
