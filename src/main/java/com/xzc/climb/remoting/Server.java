package com.xzc.climb.remoting;

import com.xzc.climb.config.provider.ProviderConfig;

public interface Server {
    void start(int port, ProviderConfig config);
    void stop();
}
