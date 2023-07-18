package org.lcm.handlermethodreturnvalue.cache.impl;

import org.lcm.handlermethodreturnvalue.cache.HandlerMethodReturnValueAnnotatedHandlerMethodCache;

import java.lang.reflect.Method;

public class DisableHandlerMethodReturnValueAnnotatedHandlerMethodCache implements HandlerMethodReturnValueAnnotatedHandlerMethodCache {
    @Override
    public boolean contains(Method method) {
        return false;
    }
}
