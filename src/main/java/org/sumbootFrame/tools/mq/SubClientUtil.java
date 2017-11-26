package org.sumbootFrame.tools.mq;

import org.sumbootFrame.tools.JedisClusterUtil;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by thinkpad on 2016/12/6.
 */
public class SubClientUtil {
    private JedisCluster jedis;//
    private JedisPubSub listener;//单listener
    public SubClientUtil(String clientId, String password, String... clusters){
        jedis = new JedisClusterUtil().cluster(password,clusters);
        listener = new PubSubListener(clientId, jedis);
    }

    public void sub(String... channel){
        jedis.subscribe(listener, channel);
    }

    public void unsubscribe(String... channel){
        listener.unsubscribe(channel);
    }
}
