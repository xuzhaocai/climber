package com.xzc.climb.common.provider;

import com.xzc.climb.common.api.SimpleCRUD;

public class SimpleCRUDImpl  implements SimpleCRUD {

    private String name ="testName" ;



    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAge() {
        return "18";
    }

    @Override
    public boolean setName(String name) {

        this.name= name;
        return true;
    }

    @Override
    public boolean invoke() {



        return false;
    }
}
