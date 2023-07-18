package org.lcm.handlermethodreturnvalue.autoconfig;

import org.lcm.handlermethodreturnvalue.cache.HandlerMethodReturnValueAnnotatedHandlerMethodCache;
import org.lcm.handlermethodreturnvalue.cache.impl.HandlerMethodReturnValueAnnotatedHandlerMethodCacheV2;
import org.lcm.handlermethodreturnvalue.factory.ReturnValueFactory;
import org.lcm.handlermethodreturnvalue.factory.impl.SimpleReturnValueFactory;
import org.lcm.handlermethodreturnvalue.handler.HandlerMethodReturnValueAnnotatedHandlerConfig;
import org.lcm.handlermethodreturnvalue.model.ReturnValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@Import({HandlerMethodReturnValueAnnotatedHandlerConfig.class})
public class HandlerMethodReturnValueAutoConfigure {
    private final WebApplicationContext webApplicationContext;
    private static final Logger logger = LoggerFactory.getLogger(HandlerMethodReturnValueAutoConfigure.class);

    static {
        logger.info("Load HandlerMethodReturnValueAutoConfigure");
        logger.info("Load HandlerMethodReturnValueAnnotatedHandlerConfig");
    }

    public HandlerMethodReturnValueAutoConfigure(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @ConditionalOnMissingBean
    @Bean
    public ReturnValueFactory<? extends ReturnValue> returnValueFactory(){
        logger.info("Init SimpleReturnValueFactory as default ReturnValueFactory");
        logger.warn("可以创建ReturnValueFactory的子类，来实现自定义的ReturnValue包装逻辑");
        return new SimpleReturnValueFactory();
    }

    @ConditionalOnMissingBean
    @Bean
    public HandlerMethodReturnValueAnnotatedHandlerMethodCache handlerMethodReturnValueAnnotatedHandlerMethodCache(){
        logger.info("Init HandlerMethodReturnValueAnnotatedHandlerMethodCacheV3 as default HandlerMethodReturnValueAnnotatedHandlerMethodCache");
        return new HandlerMethodReturnValueAnnotatedHandlerMethodCacheV2(webApplicationContext);
    }

}
