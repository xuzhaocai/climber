package com.xzc.climb.remoting;

import com.xzc.climb.config.invoker.InvokerConfig;

import java.lang.reflect.InvocationHandler;

public interface Client {


    ClimberRespose  send(ClimberRequest request);

    ClimberRespose send(ClimberRequest request, String address, InvokerConfig invocationHandler);
}
