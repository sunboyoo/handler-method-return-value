package org.lcm.handlermethodreturnvalue.factory.impl;


import org.lcm.handlermethodreturnvalue.factory.ReturnValueFactory;
import org.lcm.handlermethodreturnvalue.model.impl.SimpleReturnValue;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;

public class SimpleReturnValueFactory implements ReturnValueFactory<SimpleReturnValue> {

    @Override
    public SimpleReturnValue create(Object valueReturnedFromHandlerMethod, MethodParameter returnType) {
        SimpleReturnValue returnValue = new SimpleReturnValue();
        configReturnValue(returnValue, valueReturnedFromHandlerMethod, returnType);
        return returnValue;
    }

    @Override
    public void configReturnValue(SimpleReturnValue returnValue, Object valueReturnedFromHandlerMethod, MethodParameter returnType) {
        returnValue.setSuccess(true);
        returnValue.setData(valueReturnedFromHandlerMethod);
        returnValue.setStatus(HttpStatus.OK.name()); // "ok" for Ant-Design-Pro
        returnValue.setCode(HttpStatus.OK.value());
    }
}
