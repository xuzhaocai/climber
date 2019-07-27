package com.xzc.climb.remoting.router;

import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RoundLoadBalance  implements  LoadBalance {
    private ConcurrentMap<String, Integer>  countCache =new ConcurrentHashMap<>();
    private Random random =new Random();
    private long lastTime= 0;
    @Override
    public String getRouter(String key, TreeSet<String> providers) {
        int length = providers.size();
        String[] arr= new String[length];
        int count= 0 ;

        long now = System.currentTimeMillis();
        if (lastTime> now){
            countCache.clear();
            lastTime =now +1000*60*60*24;
        }
        Integer c = countCache.get(key);
        count= c==null?random.nextInt(100):++c;
        countCache.put(key,count);
        return arr[count%length];
    }
}
