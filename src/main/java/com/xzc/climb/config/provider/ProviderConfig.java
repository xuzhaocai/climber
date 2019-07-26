package com.xzc.climb.config.provider;

import com.xzc.climb.registry.Registry;
import com.xzc.climb.remoting.ClimberRequest;
import com.xzc.climb.remoting.ClimberRespose;
import com.xzc.climb.remoting.Server;

import java.util.HashMap;
import java.util.Map;

public class ProviderConfig {


    protected Server server;
    protected Registry registry;
    protected String zkaddress;
    protected int port;
    // key : interface  value:  impl class
    private Map<String,String> cacheMap = new HashMap<>();



    //启动
    public void start(){

        server.start(port);
    }
}
