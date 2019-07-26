package com.xzc.climb.utils;

public class AssertUtil {


    public  static  void  isNull(Object object ,String msg){
        if (object==null) throw  new ClimberException(msg);
    }


}
