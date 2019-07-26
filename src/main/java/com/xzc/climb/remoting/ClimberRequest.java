package com.xzc.climb.remoting;

import java.io.Serializable;

public class ClimberRequest implements Serializable {

    private String id;
    private String className;
    private String methodName;
    private Class[] methodParamsType;
    private Object[] methodParamsValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getMethodParamsType() {
        return methodParamsType;
    }

    public void setMethodParamsType(Class[] methodParamsType) {
        this.methodParamsType = methodParamsType;
    }

    public Object[] getMethodParamsValue() {
        return methodParamsValue;
    }

    public void setMethodParamsValue(Object[] methodParamsValue) {
        this.methodParamsValue = methodParamsValue;
    }
}
