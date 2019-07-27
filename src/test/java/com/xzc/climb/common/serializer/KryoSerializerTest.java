package com.xzc.climb.common.serializer;

import com.xzc.climb.serializer.KryoSerializer;

import java.util.Map;

public class KryoSerializerTest {


    public static void main(String[] args) {


        KryoSerializer serializer = new KryoSerializer();

        User user  =new User();
        user.setName("name");

        byte[] bytes = serializer.encode(user);

        User user00 = serializer.decode(bytes, User.class);
        System.out.println(user00);
    }

    public static  class User{

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
