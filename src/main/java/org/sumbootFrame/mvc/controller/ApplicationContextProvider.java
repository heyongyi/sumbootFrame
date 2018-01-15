package org.sumbootFrame.mvc.controller;

/**
 * Created by thinkpad on 2016/12/12.
 */
import com.google.gson.JsonObject;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.sumbootFrame.data.mao.RedisDao;
import org.sumbootFrame.tools.config.*;
import org.sumbootFrame.tools.mq.PubClientUtil;

import java.util.HashMap;
import java.util.List;

@Service
public class ApplicationContextProvider implements ApplicationListener<ContextRefreshedEvent> {
    private static ApplicationContext applicationContext = null;
    private static String subscriberCenter = null;
    private static String messagetxid = null;
    private static HashMap<String,Object> memoryCache = new HashMap<>();
    private static AppConfig appConfigStatic ;
    @Autowired
    AppConfig appconf;
    @Autowired
    PubSubConfig pubSubConfig;
    @Autowired
    RedisConfig redisConfig;
    @Autowired
    ViewsConfig viewsConfig;
    private static HashMap<String, Object> getMemcache(String memKey) {
        HashMap<String, Object> cachedParam;
        RedisDao redisDao;
        try {
            redisDao = (RedisDao) applicationContext.getBean("RedisDao");
            cachedParam = redisDao.read(appConfigStatic.getCacheChanel()+"-mem", ""+memKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return cachedParam;
    }

    public static void setMemcache(HashMap<String, Object> param, String memKey) {
        RedisDao redisDao;
        if(memKey != null){
            try {
                redisDao = (RedisDao) applicationContext.getBean("RedisDao");
                redisDao.save(appConfigStatic.getCacheChanel()+"-mem", ""+memKey, (Object)param, -1);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(applicationContext == null){
            applicationContext = event.getApplicationContext();
        }
        if(subscriberCenter == null){
            subscriberCenter = pubSubConfig.getSubscriberCenter();
        }
        if(messagetxid == null){
            messagetxid = pubSubConfig.getMessagetxid();
        }
        appConfigStatic = appconf;
        if(pubSubConfig.getInterval()>0){
            PubThread pubThread = new PubThread();
            pubThread.setDaemon(true);
            pubThread.start();
        }

    }
    private class PubThread extends Thread {
        @Override
        public void run() {
            while (true) {
                final String channel = pubSubConfig.getRegisterChannel();
                PubClientUtil pubClient ;
                if(redisConfig.getCluster() != null){
                    List<String> clusterNodes = redisConfig.getCluster().getNodes();
                    pubClient = new PubClientUtil(redisConfig.getPassword(),
                            (String[])clusterNodes.subList(0,redisConfig.getCluster().getMaxRedirects()).toArray(new String[redisConfig.getCluster().getMaxRedirects()]));
                }else{
                    pubClient = new PubClientUtil(redisConfig.getHost(),redisConfig.getPort(),redisConfig.getDatabase(),redisConfig.getPassword());
                }

                JsonObject msg = new JsonObject();

                msg.addProperty("appName",appconf.getName());
                msg.addProperty("runMode",appconf.getRunningMode());
                for(String key:viewsConfig.getUrlRouteDefault().keySet()){
                    msg.addProperty("module",key);
                }
                String executor = "";
                for(String key:viewsConfig.getUrlRoute().keySet()){
                    executor+=key+"^";
                }
                msg.addProperty("executor",executor);


                String message = msg.toString();
                pubClient.pub(channel, message);
                synchronized (this) {
                    try {
                        Thread.sleep(pubSubConfig.getInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    public static String getSubscriberCenter(){return subscriberCenter;}
    public static String getMessagetxid() {
        return messagetxid;
    }
    public static HashMap<String, Object> getMemoryCache(String memKey) {
        if(!memoryCache.containsKey(memKey)){
            memoryCache.put(memKey,getMemcache(memKey));
        }
        return (HashMap<String, Object>)memoryCache.get(memKey);
    }
    public static void setMemoryCache(String memKey,HashMap<String, Object> memVal){
        setMemcache(memVal,memKey);
        memoryCache.put(memKey,memVal);
    }
}