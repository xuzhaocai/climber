package com.xzc.climb.registry;

import java.util.Map;
import java.util.TreeSet;

public interface Registry {
   boolean register(String key , TreeSet<String> set);
   boolean register(String key, String value);
   TreeSet<String> discover(String key);
   Map<String , TreeSet<String>> discover();
   boolean remove(String key);
   boolean remove(String key , String value);

}
