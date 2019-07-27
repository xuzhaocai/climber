package com.xzc.climb.remoting.router;

import java.util.Random;
import java.util.TreeSet;

public class RandomLoadBalance  implements  LoadBalance {

    private Random random =new Random();

    @Override
    public String getRouter(String key, TreeSet<String> providers) {
        int length = providers.size();
        String[]  arr= new String[length];
        providers.toArray(arr);
        return arr[random.nextInt()];
    }
}
