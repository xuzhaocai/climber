package com.xzc.climb.config.provider;

import com.xzc.climb.registry.Registry;
import com.xzc.climb.registry.ZookeeperRegistryImpl;
import com.xzc.climb.remoting.Server;
import com.xzc.climb.remoting.netty.NettyServer;
import com.xzc.climb.serializer.KryoSerializer;
import com.xzc.climb.serializer.Serializer;
import com.xzc.climb.utils.AssertUtil;
import com.xzc.climb.utils.ClimberException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;

public class ClimberSpringProviderConfig   implements InitializingBean,DisposableBean,ApplicationContextAware {


    private int port;  // server port
    private String address;   // zk address


    private ProviderConfig config ;
    @Override
    public void destroy() throws Exception {
        if (config!= null) {
            config.stop();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (port==0 ){
            throw new ClimberException("server port must not null");
        }
        AssertUtil.isNull(address," register address must not null");
        Server  server = new NettyServer();
        Serializer serializer = new KryoSerializer();
        Registry registry  = new ZookeeperRegistryImpl(address);
        config.setRegistry(registry);
        config.setServer(server);
        config.setSerializer(serializer);
        config.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        config = new ProviderConfig();
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ClimberService.class);
        if (beanMap!= null  && beanMap.size()>0){
            Collection<Object> beans = beanMap.values();
            for (Object bean :beans){

                if(bean.getClass().getInterfaces().length==0){
                        throw new ClimberException("service must inherit interface");
                }
                String type = bean.getClass().getInterfaces()[0].getName();
                config.addProvider(type,bean);

            }
        }
    }
}
