package com.xzc.climb.registry;

import com.xzc.climb.utils.ClimberException;
import com.xzc.climb.utils.CommonUtil;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ZookeeperRegistryImpl  implements  Registry {
    private String  zkAddress;



    private ZkHandler zkHandler;
    public static  final ConcurrentMap<String , TreeSet<String>>  discoverMap= new ConcurrentHashMap<>();
    public static  final  ConcurrentMap <String , TreeSet<String >> registerMap = new ConcurrentHashMap<>();
    public  ZookeeperRegistryImpl(String zkAddress){
        this.zkAddress= zkAddress;
        zkHandler = getZkHandler();
    }

    private ZkHandler getZkHandler() {

        return new ZkHandler(zkAddress);
    }

    @Override
    public boolean register(String key, TreeSet<String> set) {






        return true;
    }

    @Override
    public boolean register(String key, String value) {
        return false;
    }

    @Override
    public TreeSet<String> discover(String key) {
        return null;
    }

    @Override
    public Map<String, TreeSet<String>> discover(Set<String> keys) {
        return null;
    }

    @Override
    public boolean remove(String key) {
        return false;
    }

    @Override
    public boolean remove(String key, String value) {
        return false;
    }

    public static void main(String[] args) {
        ZkHandler  zkHandler = new ZkHandler("127.0.0.1:2181");
        zkHandler.createNode(ZkHandler.BASE_PATH,"value");
    }




    public static  class ZkHandler{
        public static  final  String BASE_PATH= "/climber";
        public ZooKeeper zooKeeper ;
        public static  final  int SESSION_TIME_OUT = 5000;
        public Watcher  watcher;
        public String address;
        public ZkHandler(String  address){
            if (CommonUtil.isEmpty(address)) {
                throw   new ClimberException(" register server address can not null");
            }
            this.address =address;
            this.watcher=  new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    Watcher.Event.EventType type = watchedEvent.getType();
                }
            };
            createConnection();
        }

        private void  createConnection() {
            try {
                zooKeeper =  new ZooKeeper(address, SESSION_TIME_OUT, watcher);


            } catch (IOException e) {
                throw new ClimberException(e);
            }
        }


        public  boolean delete(){
            return false;
        }

        public  boolean createNode (String key , String  value ){

            try {
                zooKeeper.create(key, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
    }




}
