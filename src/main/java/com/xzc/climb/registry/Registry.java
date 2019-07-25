package com.xzc.climb.registry;

import java.util.TreeSet;

public interface Registry {

   void register(String key, String value);

   TreeSet<String> discover(String key);
}
