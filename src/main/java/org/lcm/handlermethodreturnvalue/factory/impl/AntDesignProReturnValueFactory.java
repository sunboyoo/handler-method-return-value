package org.lcm.handlermethodreturnvalue.factory.impl;

import org.lcm.handlermethodreturnvalue.factory.ReturnValueFactory;
import org.lcm.handlermethodreturnvalue.model.impl.AntDesignProReturnValue;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public class AntDesignProReturnValueFactory implements ReturnValueFactory<AntDesignProReturnValue> {
    @Override
    public AntDesignProReturnValue create(Object valueReturnedFromHandlerMethod, MethodParameter returnType) {
        AntDesignProReturnValue returnValue = new AntDesignProReturnValue();
        configReturnValue(returnValue, valueReturnedFromHandlerMethod, returnType);
        return returnValue;
    }

    @Override
    public void configReturnValue(AntDesignProReturnValue returnValue, Object valueReturnedFromHandlerMethod, MethodParameter returnType) {

        if (Objects.requireNonNull(returnType.getMethod()).getReturnType() == Page.class){
            // 可以判断returnType是否为Page<? extends Entity>
            returnValue.setPage((Page<?>) valueReturnedFromHandlerMethod);
        } else {
            // 非 Page 返回值
            returnValue.setData(valueReturnedFromHandlerMethod);
        }

        returnValue.setSuccess(true);
        returnValue.setStatus(HttpStatus.OK.name()); // "ok" for Ant-Design-Pro
        returnValue.setCode(HttpStatus.OK.value());
    }
}
