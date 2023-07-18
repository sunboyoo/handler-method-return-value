package org.lcm.handlermethodreturnvalue.disable;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Spring Cloud OpenFeign 的 RequestInterceptor
 * 向 Request Header 中添加一个 header
 *
 * 使用方法 @FeignClient(value = "product-service",configuration = DisableHandlerMethodReturnValueRequestInterceptor.class)
 *
 */
public class DisableHandlerMethodReturnValueRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(DisableHandlerMethodReturnValue.HEADER_NAME, "");
    }
}
