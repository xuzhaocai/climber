package com.xzc.climb.registry;

import com.xzc.climb.utils.ClimberException;
import com.xzc.climb.utils.CommonUtil;
import com.xzc.climb.utils.XxlZkClient;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


/**
 * by xuxueli
 */
public class ZookeeperRegistryImpl  implements  Registry {
    private String  zkAddress;

    public static  final  String zkBasePath="/climber";
    private XxlZkClient  xxlZkClient;

    private Thread refreshThread;
    private volatile boolean refreshThreadStop = false;
    public  void  init(){

        // init 创建zk 客户端
        xxlZkClient = new XxlZkClient(zkAddress, zkBasePath, null, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {

                    // session expire, close old and create new
                    if (watchedEvent.getState() == Event.KeeperState.Expired) {
                        xxlZkClient.destroy();
                        xxlZkClient.getClient();

                        // refreshDiscoveryData (all)：expire retry
                        refreshDiscoveryData(null);

                    }

                    // watch + refresh
                    String path = watchedEvent.getPath();
                    String key = pathToKey(path);
                    if (key != null) {
                        // keep watch conf key：add One-time trigger
                        xxlZkClient.getClient().exists(path, true);

                        // refresh
                        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                            // refreshDiscoveryData (one)：one change
                            refreshDiscoveryData(key);
                        } else if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {

                        }
                    }

                } catch (Exception e) {

                }
            }
        });
        // init client
        xxlZkClient.getClient();


        // refresh thread
        refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!refreshThreadStop) {
                    try {
                        TimeUnit.SECONDS.sleep(60);

                        // refreshDiscoveryData (all)：cycle check
                        refreshDiscoveryData(null);

                        // refresh RegistryData
                        refreshRegistryData();
                    } catch (Exception e) {
                        if (!refreshThreadStop) {

                        }
                    }
                }

            }
        });
        refreshThread.setName("xxl-rpc, ZkServiceRegistry refresh thread.");
        refreshThread.setDaemon(true);
        refreshThread.start();


    }

    private void refreshRegistryData(){
        if (registerMap.size() > 0) {
            for (Map.Entry<String, TreeSet<String>> item: registerMap.entrySet()) {
                String key = item.getKey();
                for (String value:item.getValue()) {
                    // make path, child path
                    String path = keyToPath(key);
                    xxlZkClient.setChildPathData(path, value, "");
                }
            }

        }
    }

    private void refreshDiscoveryData(String key){

        Set<String> keys = new HashSet<String>();
        if (key!=null && key.trim().length()>0) {
            keys.add(key);
        } else {
            if (discoverMap.size() > 0) {
                keys.addAll(discoverMap.keySet());
            }
        }

        if (keys.size() > 0) {
            for (String keyItem: keys) {

                // add-values
                String path = keyToPath(keyItem);
                Map<String, String> childPathData = xxlZkClient.getChildPathData(path);

                // exist-values
                TreeSet<String> existValues = discoverMap.get(keyItem);
                if (existValues == null) {
                    existValues = new TreeSet<String>();
                    discoverMap.put(keyItem, existValues);
                }

                if (childPathData.size() > 0) {
                    existValues.clear();
                    existValues.addAll(childPathData.keySet());
                }
            }
        }
    }

    public static  final ConcurrentMap<String , TreeSet<String>>  discoverMap= new ConcurrentHashMap<>();
    public static  final  ConcurrentMap <String , TreeSet<String >> registerMap = new ConcurrentHashMap<>();
    public  ZookeeperRegistryImpl(String zkAddress){
        this.zkAddress= zkAddress;
        init();
    }


    @Override
    public boolean register(String key, String value) {


        TreeSet<String> values = registerMap.get(key);
        if (values == null) {
            values = new TreeSet<>();
            registerMap.put(key, values);
        }
        values.add(value);

        // make path, child path
        String path = keyToPath(key);
        xxlZkClient.setChildPathData(path, value, "");
        return true;
    }

    @Override
    public TreeSet<String> discover(String key) {
        // local cache
        TreeSet<String> values = discoverMap.get(key);
        if (values == null) {

            // refreshDiscoveryData (one)：first use
            refreshDiscoveryData(key);

            values = discoverMap.get(key);
        }

        return values;
    }

    @Override
    public Map<String, TreeSet<String>> discover(Set<String> keys) {
        if (keys==null || keys.size()==0) {
            return null;
        }
        Map<String, TreeSet<String>> registryDataTmp = new HashMap<String, TreeSet<String>>();
        for (String key : keys) {
            TreeSet<String> valueSetTmp = discover(key);
            if (valueSetTmp != null) {
                registryDataTmp.put(key, valueSetTmp);
            }
        }
        return registryDataTmp;
    }



    @Override
    public boolean remove(String key, String value) {
        TreeSet<String> values = discoverMap.get(key);
        if (values != null) {
            values.remove(value);
        }
        String path = keyToPath(key);
        xxlZkClient.deleteChildPath(path, value);

        return true;
    }
    /*
     * key 2 path
     * @param   nodeKey
     * @return  znodePath
     */
    public String keyToPath(String nodeKey){
        return zkBasePath + "/" + nodeKey;
    }

    /**
     * path 2 key
     * @param   nodePath
     * @return  nodeKey
     */
    public String pathToKey(String nodePath){
        if (nodePath==null || nodePath.length() <= zkBasePath.length() || !nodePath.startsWith(zkBasePath)) {
            return null;
        }
        return nodePath.substring(zkBasePath.length()+1, nodePath.length());
    }

    public void stop() {
        if (xxlZkClient!=null) {
            xxlZkClient.destroy();
        }
        if (refreshThread != null) {
            refreshThreadStop = true;
            refreshThread.interrupt();
        }
    }
}
