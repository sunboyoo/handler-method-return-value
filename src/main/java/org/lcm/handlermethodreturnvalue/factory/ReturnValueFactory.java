package org.lcm.handlermethodreturnvalue.factory;


import org.lcm.handlermethodreturnvalue.model.ReturnValue;
import org.springframework.core.MethodParameter;

public interface ReturnValueFactory<T extends ReturnValue> {

    // For HandlerMethodReturnValueHandler
    T create(Object valueReturnedFromHandlerMethod, MethodParameter returnType);

    // For HandlerMethodReturnValueHandler
    default void configReturnValue(T returnValue, Object valueReturnedFromHandlerMethod, MethodParameter returnType){
        // do nothing
        // allow subclass to customize
    }
}
