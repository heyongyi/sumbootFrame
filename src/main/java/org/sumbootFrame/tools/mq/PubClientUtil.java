package org.sumbootFrame.tools.mq;

import org.sumbootFrame.mvc.controller.ApplicationContextProvider;
import org.sumbootFrame.tools.JedisClusterUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Set;

/**
 * Created by thinkpad on 2016/12/6.
 */

public class PubClientUtil {
    private static JedisCluster jedis;
    private static Jedis simplejedis;

    private static String SUBSCRIBE_CENTER = ApplicationContextProvider.getSubscriberCenter();
    private String MESSAGE_TXID = ApplicationContextProvider.getMessagetxid();

    public PubClientUtil(String password, String... clusters){
        if(jedis == null){
            jedis = new JedisClusterUtil().cluster(password,clusters);
        }
    }
    public PubClientUtil(String host,int port,int database,String passport){
        if(simplejedis == null){
            simplejedis = new Jedis(host,port);
            simplejedis.auth(passport);
            simplejedis.select(database);
        }
    }
    /**
     * 发布的每条消息，都需要在“订阅者消息队列”中持久
     * @param message
     */
    public void pub(String channel, String message){
        if(jedis != null){
            //每个消息，都有具有一个全局唯一的id
            //txid为了防止订阅端在数据处理时“乱序”，这就要求订阅者需要解析message
            Long txid = jedis.incr(MESSAGE_TXID); //递增
            String content = txid + "/" + message;
            //非事务
            //this.put(channel,content);//数据持久化 待定
            jedis.publish(channel, content);//为每个消息设定id，最终消息格式1000/messageContent
        }else{
            Long txid = simplejedis.incr(MESSAGE_TXID); //递增
            String content = txid + "/" + message;
            simplejedis.publish(channel, content);
        }

    }
    public void close(String channel){
        if(jedis != null){
            jedis.publish(channel, "quit");
            jedis.del(channel);//删除
        }else{
            simplejedis.publish(channel, "quit");
            simplejedis.del(channel);//删除
        }

    }

}
