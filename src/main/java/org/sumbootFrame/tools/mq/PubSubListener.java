package org.sumbootFrame.tools.mq;

import org.sumbootFrame.mvc.controller.ApplicationContextProvider;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by thinkpad on 2016/12/6.
 */

public class PubSubListener extends JedisPubSub {
    private String clientId;
    private SubHandler handler;

    public String SUBSCRIBE_CENTER = ApplicationContextProvider.getSubscriberCenter();
    public PubSubListener(){
    }
    public PubSubListener(String clientId, JedisCluster jedis){
        this.clientId = clientId;
        handler = new SubHandler(jedis);
    }

    private void message(String channel, String message){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");//可以方便地修改日期格式
        String time = dateFormat.format( now );
        System.out.println("message receive:" + message + ",channel:" + channel + "...subtime-" + time);
    }
    @Override
    public void onMessage(String channel, String message) {
        //此处我们可以取消订阅
        if(message.equalsIgnoreCase("quit")){//发布端删除channel时发送的消息
            this.unsubscribe(channel);
        }
        handler.handle(channel, message);//触发当前订阅者从自己的消息队列中移除消息
    }

    @Override
    public void onPMessage(String s, String s1, String s2) {

    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        // 将订阅者保存在一个"订阅活跃者集合中"
        System.out.println("订阅活跃者集合中");
        handler.subscribe(channel);
        System.out.println("subscribe:" + channel + ";total channels : " + subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        handler.unsubscribe(channel);
        System.out.println("unsubscribe:" + channel + ";total channels : " + subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String s, int i) {

    }

    @Override
    public void onPSubscribe(String s, int i) {

    }

    class SubHandler {

        private JedisCluster jedis;
        SubHandler(JedisCluster jedis){
            this.jedis = jedis;
        }
        public void handle(String channel, String message){
            message(channel,message);//处理当前的message
            //之后处理历史遗留message

        }
        public void subscribe(String channel){
            String key = clientId + "/" + channel;
            System.out.println(key);
            System.out.println(SUBSCRIBE_CENTER);
            boolean exist = jedis.sismember(SUBSCRIBE_CENTER,key);
            if(!exist){
                jedis.sadd(SUBSCRIBE_CENTER, key);
            }
        }

        public void unsubscribe(String channel){
            String key = clientId + "/" + channel;
            jedis.srem(SUBSCRIBE_CENTER, key);//从“活跃订阅者”集合中删除
            jedis.del(key);//删除“订阅者消息队列”  持久化
        }
    }
}
