package com.xzc.climb.common.provider;

import com.xzc.climb.common.api.SimpleCRUD;
import com.xzc.climb.config.invoker.InvokerConfig;
import com.xzc.climb.config.provider.ProviderConfig;
import com.xzc.climb.registry.LocalRegistryImpl;
import com.xzc.climb.remoting.NettyClient;
import com.xzc.climb.remoting.NettyServer;
import com.xzc.climb.serializer.KryoSerializer;

public class ProviderTest {


    public static void main(String[] args) {


        ProviderConfig config =new ProviderConfig();
        config.setPort(9999);
        config.setSerializer(new KryoSerializer());
        config.setServer(new NettyServer());
        config.setRegistry(new LocalRegistryImpl());

        config.addProvider(SimpleCRUD.class,new SimpleCRUDImpl());
        config.start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        InvokerConfig invokerConfig = new InvokerConfig();
        invokerConfig.setSerializer(new KryoSerializer());
        invokerConfig.setClient(new NettyClient());
        invokerConfig.setRegistry(new LocalRegistryImpl());
        SimpleCRUD simpleCRUD =(SimpleCRUD) invokerConfig.getInvoker(SimpleCRUD.class);
        String name = simpleCRUD.getName();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
