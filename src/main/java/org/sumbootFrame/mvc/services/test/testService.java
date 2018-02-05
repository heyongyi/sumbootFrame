package org.sumbootFrame.mvc.services.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.sumbootFrame.mvc.services.serviceAbstract;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.tools.config.SecurityConfig;
import org.sumbootFrame.utils.RETURN;

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
        return RETURN.SUCCESS;
    }

    @Override
    public ReturnUtil execute() throws Exception {
        return null;
    }
}
