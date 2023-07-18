package org.lcm.handlermethodreturnvalue.handler;

import org.lcm.handlermethodreturnvalue.annotation.HandlerMethodReturnValue;
import org.lcm.handlermethodreturnvalue.cache.HandlerMethodReturnValueAnnotatedHandlerMethodCache;
import org.lcm.handlermethodreturnvalue.disable.DisableHandlerMethodReturnValue;
import org.lcm.handlermethodreturnvalue.factory.ReturnValueFactory;
import org.lcm.handlermethodreturnvalue.model.ReturnValue;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
    简单代理模式 - 或者包装模式
    使用自定义的 HandlerMethodReturnValueHandler 代理 RequestResponseBodyMethodProcessor
    它们都实现了同样的接口 HandlerMethodReturnValueHandler
    https://mp.weixin.qq.com/s/8aMz07rOF5LuclnBaI_p5g

    RequestResponseBodyHandlerMethodReturnValueHandler 是被 RequestResponseBodyHandlerMethodReturnValueHandlerConfig
    创建一个对象，并加入到 HandlerMethodReturnValueHandler List 中的。
    RequestResponseBodyHandlerMethodReturnValueHandler 并不是@Bean等方式被加入到容器中的。
* */
public class HandlerMethodReturnValueAnnotatedHandler implements HandlerMethodReturnValueHandler {
    // 被代理的对象 -
    // 这是 HandlerMethodReturnValueHandler 的实现类之一，这个主要用来处理返回 JSON 的情况。
    private final RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;
    // 缓存
    private final HandlerMethodReturnValueAnnotatedHandlerMethodCache handlerMethodReturnValueAnnotatedHandlerMethodCache;
    private final ReturnValueFactory<? extends ReturnValue> returnValueFactory;

    // Spring官方 - 依赖注入，始终以构造器模式注入
    // Spring官方 - 必需的依赖，始终以 Assert 检查
    public HandlerMethodReturnValueAnnotatedHandler(
            RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor,
            HandlerMethodReturnValueAnnotatedHandlerMethodCache handlerMethodReturnValueAnnotatedHandlerMethodCache,
            ReturnValueFactory<? extends ReturnValue> returnValueFactory) {
        this.returnValueFactory = returnValueFactory;
        Assert.notNull(requestResponseBodyMethodProcessor, "RequestResponseBodyMethodProcessor must not be null.");
        this.requestResponseBodyMethodProcessor = requestResponseBodyMethodProcessor;
        this.handlerMethodReturnValueAnnotatedHandlerMethodCache = handlerMethodReturnValueAnnotatedHandlerMethodCache;
    }

    /**
     * RequestResponseBodyMethodProcessor 源码
     * supportsReturnType：从这个方法中可以看到，这里支持有 @ResponseBody 注解的接口
     * handleReturnValue：这是具体的处理逻辑，首先 mavContainer 中设置 requestHandled 属性为 true，表示这里处理完成后就完了，
     * 以后不用再去找视图了，然后分别获取 inputMessage 和 outputMessage，调用 writeWithMessageConverters 方法进行输出，
     * writeWithMessageConverters 方法是在父类中定义的方法，这个方法比较长，核心逻辑就是调用确定输出数据、确定 MediaType，
     * 然后通过 HttpMessageConverter 将 JSON 数据写出去即可
     */

    // 这个处理器是否支持相应的返回值类型
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        /*
         * 检查 Request Header 中是否包含 DISABLE 字段
         * 如果有，则不会再包装
         */
        if (DisableHandlerMethodReturnValue.existsInHttpRequestHeader()){
            return false;
        }

        if (this.handlerMethodReturnValueAnnotatedHandlerMethodCache != null) {
            // 使用缓存
            return this.handlerMethodReturnValueAnnotatedHandlerMethodCache.contains(returnType.getMethod());
        } else {
            // 不使用缓存
            // 被代理对象执行检查
            boolean hasResponseBodyAnnotation = this.requestResponseBodyMethodProcessor.supportsReturnType(returnType);

            // 检查是否在类上或者方法上包含注解 @ResponseResult
            boolean hasResponseResultAnnotation =
                    AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), HandlerMethodReturnValue.class) ||
                            returnType.hasMethodAnnotation(HandlerMethodReturnValue.class);

            // 可以有两种包装逻辑
            // (1) 自动把所有的@ResponseBody的返回值包装成Result对象。优点：无需注解、无代码侵入。缺点：不可以设置哪些Controller的返回不包装
            // (2) 在需要包装成Result对象的Controller上加注解@ResponseResult。优点：可以自定义包装或者不包装、看到注解就知道包装了。缺点：需要注解、有代码侵入
            // 参考Spring的@ResponseBody需要自己写上去，所以决定使用第(2)中方式。
            return hasResponseBodyAnnotation && hasResponseResultAnnotation;
        }
    }

    // 对方法返回值进行处理
    /*
        可以很好地处理以下特殊类型的返回值
            (1) String - 正常包装
            (2) void - 正常包装
            (3) ReturnValue - 已经包装，不需要重复包装
            (4) Resource - 不包装
    * */
    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        if (returnValue instanceof ReturnValue || returnType instanceof Resource){
            // 如果 Controller 方法的返回值已经是ReturnValue类型的数据，则不再进行包装。
            // 如果 Controller 方法的返回值是 Resource 类型的资源数据，则不进行包装。
            this.requestResponseBodyMethodProcessor.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            // 将 returnValue 包装为 ReturnValue 对象
            ReturnValue newReturnValue = returnValueFactory.create(returnValue, returnType);
            // 被代理对象执行操作
            this.requestResponseBodyMethodProcessor.handleReturnValue(newReturnValue, returnType, mavContainer, webRequest);
        }
    }
}
