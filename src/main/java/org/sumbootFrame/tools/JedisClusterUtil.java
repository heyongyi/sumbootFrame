package org.sumbootFrame.tools;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class JedisClusterUtil {
    private Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();

    private JedisCluster jc;

    private static JedisPoolConfig poolConfig = new JedisPoolConfig();

    public JedisCluster cluster(String password,String... clusters){

        for(String cluster : clusters){
            String[] node = cluster.split(":");
            jedisClusterNodes.add(new HostAndPort(node[0], Integer.parseInt(node[1]) ));
            System.out.println("node:" + node[0]+"  port:"+node[1] );
        }
        System.out.println("password:" + password );

        // 3个master 节点
        jc = new JedisCluster(jedisClusterNodes,300,300,1,password,poolConfig);

        return jc;
    }
}