package com.xzc.climb.common.provider;

import com.xzc.climb.common.api.SimpleCRUD;
import com.xzc.climb.config.invoker.InvokerConfig;
import com.xzc.climb.config.provider.ProviderConfig;
import com.xzc.climb.registry.LocalRegistryImpl;
import com.xzc.climb.remoting.NettyClient;
import com.xzc.climb.remoting.NettyServer;
import com.xzc.climb.serializer.KryoSerializer;

import java.util.concurrent.locks.LockSupport;

public class ProviderTest {


    public static void main(String[] args) {
        LocalRegistryImpl localRegistry = new LocalRegistryImpl();
        ProviderConfig config =new ProviderConfig();
        config.setPort(9999);
        config.setSerializer(new KryoSerializer());
        config.setServer(new NettyServer());
        config.setRegistry(localRegistry);

        config.addProvider(SimpleCRUD.class,new SimpleCRUDImpl());
        config.start();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InvokerConfig invokerConfig = new InvokerConfig();
        invokerConfig.setSerializer(new KryoSerializer());
        invokerConfig.setClient(new NettyClient());
        invokerConfig.setRegistry(localRegistry);

        SimpleCRUD simpleCRUD =(SimpleCRUD) invokerConfig.getInvoker(SimpleCRUD.class);


        new Thread(()->{
            String name = Thread.currentThread().getName();
            while (true) {
                System.out.println("thread:  " + name + "--->" + simpleCRUD.getName());

            }
        },"t1").start();



        new Thread(()->{
            String name = Thread.currentThread().getName();
            while (true) {
                System.out.println("thread:  " + name + "--->" + simpleCRUD.getAge());

            }
        },"t2").start();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
