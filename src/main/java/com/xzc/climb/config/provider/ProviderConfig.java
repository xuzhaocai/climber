package com.xzc.climb.config.provider;

import com.xzc.climb.registry.Registry;
import com.xzc.climb.remoting.Server;

import java.util.HashMap;
import java.util.Map;

public class ProviderConfig {


    private Server server;
    private Registry registry;
    private String zkaddress;

    private int port;
    // key : interface  value:  impl class
    private Map<String,String> cacheMap = new HashMap<>();



    //启动
    public void start(){

        //启动服务
        server.start(port);

        //注册



    }

    public void stop(){




    }

}
