package org.sumbootFrame.mvc.services.rpc;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.sumbootFrame.data.dao.primary.PrimaryCommDAO;
import org.sumbootFrame.mvc.services.serviceRpcAbstract;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.utils.RETURN;

import java.util.HashMap;

/**
 * Created by thinkpad on 2018/2/2.
 */
@Service
@Scope("request")
public class testServiceRpc extends serviceRpcAbstract {
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

        return RETURN.SUCCESS;
    }

    @Override
    public ReturnUtil execute() throws Exception {
        PrimaryCommDAO primaryCommDAO = this.getDaoFactory().getMapper(PrimaryCommDAO.class);
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
