package org.sumbootFrame.mvc.controller;

/**
 * Created by thinkpad on 2016/12/12.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.sumbootFrame.tools.config.PubSubConfig;
import org.sumbootFrame.tools.config.RedisConfig;
import org.sumbootFrame.tools.mq.PubClientUtil;
import org.sumbootFrame.tools.mq.PubSubListener;
import org.sumbootFrame.tools.mq.SubClientUtil;

import java.util.List;

@Service
public class ApplicationContextProvider implements ApplicationListener<ContextRefreshedEvent> {
    private static ApplicationContext applicationContext = null;
    private static String subscriberCenter = null;
    private static String messagetxid = null;
    @Autowired
    PubSubConfig pubSubConfig;
    @Autowired
    RedisConfig redisConfig;

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
        if(redisConfig.getCluster() != null){
            Thread subThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String[] subChannels = pubSubConfig.getSubChannel().split(",");
                    List<String> clusterNodes = redisConfig.getCluster().getNodes();
                    if(subChannels.length > 0 && clusterNodes.size()>0){
                        SubClientUtil SubClient = new SubClientUtil(pubSubConfig.getClient(),
                                redisConfig.getPassword(),
                                (String[])clusterNodes.subList(0,redisConfig.getCluster().getMaxRedirects()).toArray());
                        SubClient.sub(subChannels);
                    }
                }
            });
            subThread.setDaemon(true);
            subThread.start();

            final String channel = pubSubConfig.getRegisterChannel();

            List<String> clusterNodes = redisConfig.getCluster().getNodes();
            PubClientUtil pubClient = new PubClientUtil(redisConfig.getPassword(),(String[])clusterNodes.subList(0,redisConfig.getCluster().getMaxRedirects()).toArray(new String[redisConfig.getCluster().getMaxRedirects()]));

            String message = "pubtime-1";
            pubClient.pub(channel, message);
        }
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    public static String getSubscriberCenter(){return subscriberCenter;}
    public static String getMessagetxid() {
        return messagetxid;
    }
}