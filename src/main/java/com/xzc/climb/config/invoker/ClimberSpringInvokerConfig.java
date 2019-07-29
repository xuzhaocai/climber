package com.xzc.climb.config.invoker;

import com.xzc.climb.registry.ZookeeperRegistryImpl;
import com.xzc.climb.remoting.Client;
import com.xzc.climb.remoting.netty.NettyClient;
import com.xzc.climb.serializer.KryoSerializer;
import com.xzc.climb.serializer.Serializer;
import com.xzc.climb.utils.AssertUtil;
import com.xzc.climb.utils.ClimberException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ClimberSpringInvokerConfig extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean,DisposableBean {

    private  String zkAddress;
    public  ClimberSpringInvokerConfig (String zkAddress){
        this.zkAddress=zkAddress;
    }
    private InvokerConfig  config ;

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public ClimberSpringInvokerConfig(){}
    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {

        Set<String >  keys  = new HashSet<>();
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(ClimberReference.class)){
                    Class<?> fieldType = field.getType();
                    if (!fieldType.isInterface()){
                        throw  new ClimberException("reference  must is interface");
                    }
                    AssertUtil.isNull(config,"the configration is null");
                    Object proxy = config.getInvoker(fieldType);
                    field.setAccessible(true);
                    field.set(bean,proxy);
                    keys.add(fieldType.getName());
                }
            }
        });
        if(config.getRegistry() != null){
            config.getRegistry().discover(keys);
        }
        return super.postProcessAfterInstantiation(bean,beanName);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if(config!= null) {
            AssertUtil.isNull(zkAddress, "zookeeper address can not null");
            ZookeeperRegistryImpl zookeeperRegistry = new ZookeeperRegistryImpl(zkAddress);
            Serializer serializer = new KryoSerializer();
            Client client = new NettyClient();
            config = new InvokerConfig();
            config.setClient(client);
            config.setRegistry(zookeeperRegistry);
            config.setSerializer(serializer);
            config.start();
        }
    }
    @Override
    public void destroy() throws Exception {
        if (config!= null){
            config.stop();
        }
    }
}
