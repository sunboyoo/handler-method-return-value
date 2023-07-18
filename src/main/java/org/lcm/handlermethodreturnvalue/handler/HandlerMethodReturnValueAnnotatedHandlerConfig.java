package org.lcm.handlermethodreturnvalue.handler;

import org.lcm.handlermethodreturnvalue.cache.HandlerMethodReturnValueAnnotatedHandlerMethodCache;
import org.lcm.handlermethodreturnvalue.factory.ReturnValueFactory;
import org.lcm.handlermethodreturnvalue.model.ReturnValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HandlerMethodReturnValueAnnotatedHandlerConfig implements InitializingBean {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    // 缓存
    private final HandlerMethodReturnValueAnnotatedHandlerMethodCache handlerMethodReturnValueAnnotatedHandlerMethodCache;
    private static final Logger logger = LoggerFactory.getLogger(HandlerMethodReturnValueAnnotatedHandlerConfig.class);

    private final ReturnValueFactory<? extends ReturnValue> returnValueFactory;
    public HandlerMethodReturnValueAnnotatedHandlerConfig(RequestMappingHandlerAdapter requestMappingHandlerAdapter,
                                                          HandlerMethodReturnValueAnnotatedHandlerMethodCache handlerMethodReturnValueAnnotatedHandlerMethodCache,
                                                          ReturnValueFactory<? extends ReturnValue> returnValueFactory) {
        this.returnValueFactory = returnValueFactory;
        Assert.notNull(requestMappingHandlerAdapter, "RequestMappingHandlerAdapter must not be null.");
        Assert.notNull(handlerMethodReturnValueAnnotatedHandlerMethodCache, "ResponseResultAnnotatedHandlerMethodCache must not be null.");
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
        this.handlerMethodReturnValueAnnotatedHandlerMethodCache = handlerMethodReturnValueAnnotatedHandlerMethodCache;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<HandlerMethodReturnValueHandler> originalReturnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> customAndOriginalReturnValueHandlers = new ArrayList<>();

        Assert.notNull(originalReturnValueHandlers, "returnValueHandlers must not be null.");

        for (HandlerMethodReturnValueHandler originalHandler : originalReturnValueHandlers){
            if (originalHandler instanceof RequestResponseBodyMethodProcessor) {
                // 把自定义的 handler 放在 RequestResponseBodyMethodProcessor 的前面
                // 自定义的 handler 能处理的则优先处理
                // 自定义的 handler 无法处理的，再检查 RequestResponseBodyMethodProcessor 能否处理
                customAndOriginalReturnValueHandlers.add(new HandlerMethodReturnValueAnnotatedHandler(
                        (RequestResponseBodyMethodProcessor) originalHandler,
                        handlerMethodReturnValueAnnotatedHandlerMethodCache,
                        returnValueFactory));
            }
            customAndOriginalReturnValueHandlers.add(originalHandler);
        }

        logger.info("HandlerMethodReturnValueHandlers:");
        logger.info(">>> Total: " + customAndOriginalReturnValueHandlers.size());
        for (int i = 0; i < customAndOriginalReturnValueHandlers.size(); i++){
            logger.info(String.format("%4d: ", i) + customAndOriginalReturnValueHandlers.get(i).toString());
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(Collections.unmodifiableList(customAndOriginalReturnValueHandlers));
    }
}
