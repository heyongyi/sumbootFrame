package org.sumbootFrame.data.mao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.sumbootFrame.mvc.interfaces.IDao;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by thinkpad on 2017/9/12.
 */
@Service("RedisDao")
public class RedisDao implements IDao{
    @Autowired
    private RedisTemplate<Serializable, Serializable> redisTemplate;

    public RedisDao() {
    }
    public RedisDao(RedisTemplate<Serializable, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String prefix,final String key, Object param,long timeout) {
        HashOperations<Serializable, String, String> opsForHash = redisTemplate.opsForHash();
        ObjectMapper mapper = new ObjectMapper();
        String json=null;
        try {
            json =  mapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        opsForHash.put(prefix, key, json);
        if(timeout > 0) {
            redisTemplate.expire(prefix, timeout, TimeUnit.SECONDS);
        }
    }

    public HashMap<String, Object> read(String prefix, final String key) {
        ObjectMapper mapper = new ObjectMapper();
        HashOperations<Serializable, String, String> opsForHash = redisTemplate.opsForHash();
        String json = opsForHash.get(prefix, key);
        if (json != null) {
            try {
                HashMap<String, Object> map = mapper.readValue(json, HashMap.class);
//                try{
//                    for(String k : map.keySet()){
//                        System.out.println("key="+k+" values="+map.get(k).toString());
//                    }
//                }catch(Exception e){
//
//                    System.out.println("redis read prefix="+prefix+"   key="+key);
//                }
                return map;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object read(String prefix, final String key, Class<?> cls) {
        ObjectMapper mapper = new ObjectMapper();
        HashOperations<Serializable, String, String> opsForHash = redisTemplate.opsForHash();
        String json = opsForHash.get(prefix, key);
        if (json != null) {
            try {
                Object list = mapper.readValue(json, cls);
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void delete(String prefix,final String key) {
        HashOperations<Serializable, String, HashMap<String, Object>> opsForHash = redisTemplate
                .opsForHash();
        opsForHash.delete(prefix, key);
    }
}
