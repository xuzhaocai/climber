package com.xzc.climb.utils;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AssertUtil {


    public  static  void  isNull(Object object ,String msg){
        if (object==null) throw  new ClimberException(msg);
    }


    public  static  void  isNullOrEmpty(Set object , String msg){





       if (object==null || object.size()==0 ) throw  new ClimberException(msg);
    }

}
