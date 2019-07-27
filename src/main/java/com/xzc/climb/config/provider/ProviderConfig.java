package com.xzc.climb.config.provider;

import com.xzc.climb.registry.Registry;
import com.xzc.climb.remoting.ClimberRequest;
import com.xzc.climb.remoting.ClimberRespose;
import com.xzc.climb.remoting.Server;
import com.xzc.climb.serializer.Serializer;
import com.xzc.climb.utils.AssertUtil;
import com.xzc.climb.utils.CommonUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProviderConfig {



    protected Serializer serializer;
    protected Server server;
    protected Registry registry;
    protected int port;
    // key : interface  value:  impl class
    private Map<String,String> cacheMap = new HashMap<>();
    //缓存着 bean的  map
    private Map<String , Object>  beanMap = new HashMap<>();
    public void addProvider(Class clazz,Object bean){
        String name = clazz.getName();
        beanMap.put(name,bean);
    }
    //启动
    public void start(){
        server.start(port,this);




    }
    public  ClimberRespose  doInvoke(ClimberRequest request){
        ClimberRespose respose = new ClimberRespose();
        AssertUtil.isNull(request ,"the requst info is not null");
        String id = request.getId();
        respose.setId(id);
        // 接口名称
        String interfaceName = request.getClassName();

        if (CommonUtil.isEmpty(interfaceName)){
            excetionInfoProcess(respose,"interface is not null");
            return respose;
        }
        Object o = beanMap.get(interfaceName);
        if (o==null){
            excetionInfoProcess(respose,interfaceName+" impl bean not exist");
            return respose;
        }
        Class<?> clazz = o.getClass();
        try {
            Method method = clazz.getMethod(request.getMethodName(), request.getMethodParamsType());
            Object result = method.invoke(o, request.getMethodParamsValue());
            respose.setStateCode(200);
            respose.setResult(result);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            excetionInfoProcess(respose,"NoSuchMethodException");
        } catch (IllegalAccessException e) {
            excetionInfoProcess(respose,"IllegalAccessException");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            excetionInfoProcess(respose,"InvocationTargetException");
            e.printStackTrace();
        }
        return  respose;
    }
    public void excetionInfoProcess(ClimberRespose respose ,String msg){
        respose.setExceptionInfo(msg);
        respose.setResult(null);
        respose.setStateCode(500);
    }



    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Map<String, String> getCacheMap() {
        return cacheMap;
    }

    public void setCacheMap(Map<String, String> cacheMap) {
        this.cacheMap = cacheMap;
    }

    public Map<String, Object> getBeanMap() {
        return beanMap;
    }

    public void setBeanMap(Map<String, Object> beanMap) {
        this.beanMap = beanMap;
    }


}
