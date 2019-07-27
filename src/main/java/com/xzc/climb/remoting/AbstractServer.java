package com.xzc.climb.remoting;

import com.xzc.climb.config.provider.ProviderConfig;
import com.xzc.climb.registry.Registry;
import com.xzc.climb.utils.CommonUtil;

import java.util.Map;
import java.util.Set;

public abstract class AbstractServer  implements  Server {
    protected ProviderConfig config;
    protected  Thread registerThread;
    protected  volatile  boolean isStop;
    void  doStart(){
        // 进行注册
        Map<String, Object> beanMap = config.getBeanMap();
        String ipAndPort = CommonUtil.getHostAndPort(config.getPort());
        registerThread = new Thread(()->{
            Set<String> keys = beanMap.keySet();
            Registry registry = config.getRegistry();
            for (String key : keys) {
                registry.register(key, ipAndPort);
            }
            while (!isStop) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                keys = beanMap.keySet();
                for (String key : keys) {
                    registry.register(key, ipAndPort);
                }
                System.out.println("server register thread runing.....");

            }
        });

        registerThread.setName("server-register-thread");
        registerThread.setDaemon(true);
        registerThread.start();
    }
    void  onStop(){
        if (registerThread!=null && registerThread.isAlive()){
            registerThread.interrupt();
        }
        Map<String, Object> beanMap = config.getBeanMap();
        Set<String> keySet = beanMap.keySet();
        String ipAndPort = CommonUtil.getHostAndPort(config.getPort());


        Registry registry = config.getRegistry();
        for (String key :keySet) {
            registry.remove(key,ipAndPort);
        }

    }
}
