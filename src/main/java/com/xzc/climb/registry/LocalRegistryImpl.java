package com.xzc.climb.registry;

import com.xzc.climb.utils.AssertUtil;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalRegistryImpl  implements  Registry {
    private ConcurrentMap<String, TreeSet<String>> registerData  =new ConcurrentHashMap<>();
    


    //注册
    @Override
    public boolean register(String key, String value) {
        AssertUtil.isNull(key,"register interface info is not null");
        AssertUtil.isNull(value,"register impl class  is not null");

        TreeSet<String> set = registerData.get(key);
        if (set==null ){
            set=new TreeSet<>();
            registerData.put(key,set);
        }
        set.add(value);

        return true;
    }
    //发现
    @Override
    public TreeSet<String> discover(String key) {
        AssertUtil.isNull(key,"register interface info is not null");
        return registerData.get(key);
    }

    @Override
    public Map<String, TreeSet<String>> discover(Set<String> keys) {
        return registerData;
    }

    @Override
    public  boolean remove(String key, String value) {
        TreeSet<String> set = registerData.get(key);
        if (set!=null){
            set.remove(value);
        }

        return true;
    }

}
