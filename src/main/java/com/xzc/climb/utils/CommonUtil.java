package com.xzc.climb.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class CommonUtil {

    public static boolean isEmpty(String s){
        return s==null|| s.length()==0;
    }
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }


    public static  String getIP(){
        InetAddress inetAddress  = null ;
        try {
            inetAddress=InetAddress.getLocalHost();

            String ip = inetAddress.getHostAddress();
            return ip;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static  String getHostAndPort(int port){
        return getIP()+":"+port;
    }

    public static void main(String[] args) {
        System.out.println(getIP());
    }
}
