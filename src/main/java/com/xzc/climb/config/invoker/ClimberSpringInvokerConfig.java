package com.xzc.climb.config.invoker;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ClimberSpringInvokerConfig  implements InitializingBean,DisposableBean {

    private  String zkAddress;
    ClimberSpringInvokerConfig (){


    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }



    @Override
    public void destroy() throws Exception {

    }
}
