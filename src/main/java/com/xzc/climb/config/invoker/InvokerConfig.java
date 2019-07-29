package com.xzc.climb.config.invoker;

import com.xzc.climb.config.Config;
import com.xzc.climb.registry.Registry;
import com.xzc.climb.remoting.Client;
import com.xzc.climb.remoting.ClimberRequest;
import com.xzc.climb.remoting.ClimberRespose;
import com.xzc.climb.remoting.ResposeFuture;
import com.xzc.climb.serializer.Serializer;
import com.xzc.climb.utils.AssertUtil;
import com.xzc.climb.utils.ClimberException;
import com.xzc.climb.utils.CommonUtil;
import javafx.beans.binding.ObjectExpression;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class InvokerConfig  implements Config {

    protected Serializer serializer;
    protected Registry registry;
    protected Client client;
    protected  InvokerConfig invokerConfig;
    public  InvokerConfig(){
        this.invokerConfig=this;
    }
    @Override
    public void start(){
        checkParams();
    }

    @Override
    public void stop() {

    }

    public  void  checkParams(){
        AssertUtil.isNull(serializer,"serializer  can not null");
        AssertUtil.isNull(serializer,"registry  can not null");
        AssertUtil.isNull(serializer,"client  can not null");
    }
    private  ConcurrentMap<String , ResposeFuture>  resposeFutures =new ConcurrentHashMap<>();
    public void notifyResposeFuture(ClimberRespose respose){
        if (respose==null||respose.getId()==null){
            return;
        }
        ResposeFuture resposeFuture = resposeFutures.get(respose.getId());
        resposeFuture.notifyResponse(respose);
    }
    public void setResposeFuture(String id, ResposeFuture resposeFuture){
        resposeFutures.put(id,resposeFuture);
    }
    public void removeResposeFuture(String id){
        resposeFutures.remove(id);
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
                long start = System.currentTimeMillis();
                ResposeFuture resposeFuture  =new ResposeFuture(request,invokerConfig);
                client.send(request, address, invokerConfig);
                ClimberRespose respose = resposeFuture.get(100000, TimeUnit.MILLISECONDS);
                System.out.println("耗时："+(System.currentTimeMillis()-start)+"ms");
                if (respose.getStateCode()==500){
                    throw  new ClimberException(respose.getExceptionInfo());
                }
                return respose.getResult();
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


    public InvokerConfig getInvokerConfig() {
        return invokerConfig;
    }

    public void setInvokerConfig(InvokerConfig invokerConfig) {
        this.invokerConfig = invokerConfig;
    }
}
