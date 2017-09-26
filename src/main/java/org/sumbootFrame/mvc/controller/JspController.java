package org.sumbootFrame.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.sumbootFrame.data.mao.RedisDao;
import org.sumbootFrame.tools.config.AppConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thinkpad on 2017/9/25.
 */
@Controller
public class JspController {
    @Autowired
    ApplicationContext context;
    @Autowired
    AppConfig appconf;
    public HashMap<String, Object> getCache(String cacheToken) {
        HashMap<String, Object> cachedParam;
        RedisDao redisDao;
        try {
            redisDao = (RedisDao) context.getBean("RedisDao");
            cachedParam = redisDao.read(appconf.getCacheChanel(), ""+cacheToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return cachedParam;
    }
    @RequestMapping(value = "/{module}/{executor}_jsp",method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView jspCore(@PathVariable String module,
                                @PathVariable(value = "executor") String executor,
                                @RequestParam(value = "redirecttoken", required = false) String redirecttoken,
                                Map<String,Object> map){
        if(redirecttoken != null){
            map.put("inpool",this.getCache(redirecttoken).get("inpool"));
            map.put("dataBody",this.getCache(redirecttoken).get("dataBody"));
            map.put("dataHead",this.getCache(redirecttoken).get("dataHead"));
            return new ModelAndView(((HashMap)(map.get("dataBody"))).get("jsp").toString()) ;
        }else{
            return new ModelAndView(executor);
        }

    }
}
