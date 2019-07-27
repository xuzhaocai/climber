package com.xzc.climb.config.invoker;

import com.xzc.climb.registry.Registry;
import com.xzc.climb.remoting.Client;
import com.xzc.climb.remoting.ClimberRequest;
import com.xzc.climb.remoting.ClimberRespose;
import com.xzc.climb.serializer.Serializer;
import com.xzc.climb.utils.AssertUtil;
import com.xzc.climb.utils.CommonUtil;
import javafx.beans.binding.ObjectExpression;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InvokerConfig {

    protected Serializer serializer;
    protected Registry registry;

    protected Client client;
    private ConcurrentMap<String ,TreeSet<String>> discoverMap = new ConcurrentHashMap<>();

    protected  InvokerConfig invokerConfig;
    public  InvokerConfig(){
        this.invokerConfig=this;
    }

    public  Object getInvoker(Class clazz){
        return  Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object  result= null;
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();

                ClimberRequest request = new ClimberRequest();
                request.setId(CommonUtil.getUUID());


                request.setClassName(clazz.getName());


                request.setMethodName(methodName);
                request.setMethodParamsType(parameterTypes);
                request.setMethodParamsValue(args);
                String key = clazz.getName();
                TreeSet<String>  ips = registry.discover(key);
                AssertUtil.isNullOrEmpty(ips,"not find provider");

                String address = ips.first();





                ClimberRespose respose= client.send(request, address, invokerConfig);
                System.out.println(respose);

                if(respose!=null){

                    result= respose.getResult();
                }
                return result;
            }
        });


    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;

    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ConcurrentMap<String, TreeSet<String>> getDiscoverMap() {
        return discoverMap;
    }

    public void setDiscoverMap(ConcurrentMap<String, TreeSet<String>> discoverMap) {
        this.discoverMap = discoverMap;
    }

    public InvokerConfig getInvokerConfig() {
        return invokerConfig;
    }

    public void setInvokerConfig(InvokerConfig invokerConfig) {
        this.invokerConfig = invokerConfig;
    }
}
