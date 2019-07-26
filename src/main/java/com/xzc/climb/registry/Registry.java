package com.xzc.climb.registry;

import java.util.TreeSet;

public interface Registry {
   boolean register(String key , TreeSet<String> set);
   boolean register(String key, String value);
   TreeSet<String> discover(String key);
   
   boolean remove(String key);
   boolean remove(String key , String value);

   void init();
}
