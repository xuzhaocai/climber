package com.xzc.climb.utils;

public class ClimberException extends RuntimeException {
    public ClimberException(String msg){
        super(msg);
    }

    public ClimberException(){

    }
    public ClimberException(Exception e){
        super(e);
    }
}
