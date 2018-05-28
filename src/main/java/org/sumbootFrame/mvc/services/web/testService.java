package org.sumbootFrame.mvc.services.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.sumbootFrame.data.dao.primary.PrimaryCommDAO;
import org.sumbootFrame.mvc.services.serviceAbstract;
import org.sumbootFrame.tools.HttpClientUtil;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.tools.config.SecurityConfig;
import org.sumbootFrame.utils.RETURN;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thinkpad on 2018/2/2.
 */
@Service
@Scope("request")
public class testService extends serviceAbstract {
    @Autowired
    private SecurityConfig securityConfig;
    @Override
    public ReturnUtil init() throws Exception {
        return null;
    }

    @Override
    public ReturnUtil query() throws Exception {
//        for(String key:securityConfig.getXssMap().keySet()){
//            this.getoutpool().put(key,securityConfig.getXssMap().get(key));
//        }
        for(String key:this.getinpool().keySet()){
            this.getoutpool().put(key,this.getinpool().get(key));
        }
//        HashMap<String,Object> requestparam = new HashMap<>();
//        requestparam.put("request","testService to testServiceRpc");
//        Map<String,Object> responsemap = HttpClientUtil.jhttprequest("http://localhost:9090/sumboot/test?deal-type=set1",new HashMap<>(),"ticket");
//        JsonParser parser=new JsonParser();  //创建JSON解析器
//        JsonObject object=(JsonObject) parser.parse(responsemap.get("httpbody").toString());
//        JsonObject dataHead = object.get("dataHead").getAsJsonObject();
//        if(!dataHead.get("success").getAsBoolean()){
//            return RETURN.EXTDEAL_NO_SUCCESS.setStateDetail("调用订单审核接口失败，原因："+dataHead.get("stateMsg").getAsString());
//        }
//        JsonObject dataBody = object.get("dataBody").getAsJsonObject();
//        ObjectMapper mapper = new ObjectMapper();
//        HashMap<String,Object> dataBodyMap = mapper.readValue((String) dataBody.toString(), HashMap.class);
//        for(String key:dataBodyMap.keySet()){
//            this.getoutpool().put(key,dataBodyMap.get(key));
//        }

        return RETURN.SUCCESS;
    }

    @Override
    public ReturnUtil execute() throws Exception {
        PrimaryCommDAO primaryCommDAO = (PrimaryCommDAO)this.getDaoFactory().get("primaryCommDAO");
        HashMap<String,Object> user = new HashMap<>();

        user.put("name","何永毅");
        user.put("sex","01");
        user.put("age","28");
        user.put("tel","18811111111");
        user.put("address","aaaaaaaa");

        primaryCommDAO.insertuser(user);
        return RETURN.SUCCESS;
    }
}
