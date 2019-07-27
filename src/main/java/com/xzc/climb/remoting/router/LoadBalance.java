package com.xzc.climb.remoting.router;

import java.util.TreeSet;

public interface LoadBalance {
    String getRouter(String  key, TreeSet<String> providers);
}
