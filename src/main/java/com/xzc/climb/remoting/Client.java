package com.xzc.climb.remoting;

import com.xzc.climb.config.invoker.InvokerConfig;

import java.lang.reflect.InvocationHandler;

public interface Client {



    void send(ClimberRequest request, String address, InvokerConfig invocationHandler);
}
