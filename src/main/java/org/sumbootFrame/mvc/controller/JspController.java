package org.sumbootFrame.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.sumbootFrame.data.mao.RedisDao;
import org.sumbootFrame.tools.config.AppConfig;
import org.sumbootFrame.tools.config.ResponceConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thinkpad on 2017/9/25.
 */
@Controller
@Scope("request")
public class JspController {
    @Autowired
    ApplicationContext context;
    @Autowired
    AppConfig appconf;
    @Autowired
    ResponceConfig responceconf;
    public HashMap<String, Object> getRedirectCache(String cacheToken) {
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
    private void handleResponseHeader(HttpServletResponse response, String referer) {
        if (referer != null) {
            String[] responseHeaderOrigin = responceconf.getHeader().getOrigin().split(",");
            for (String origin : responseHeaderOrigin) {
                //1、如果orgin配置为 http://*.hearglobal.com 校验逻辑如下
                //2、比对请求referer 的一级域名部分是否同orgin 的一级域名部分相同。
                //3、如果相同，则将orgin http://* 替换成referer的二级域名部分作为origin返回
                if (origin.startsWith("http://*.")||origin.startsWith("https://*.")) {
                    if (referer.substring(referer.indexOf(".") + 1).startsWith(origin.substring(origin.indexOf(".") + 1))) {
                        //由于origin不支持http://*.hearglobal.com配置，所以将http://* 替换成 referer的二级域名部分。
                        origin = origin.replace(origin.substring(0, origin.indexOf(".")), referer.substring(0, referer.indexOf(".")));
                        response.setHeader("Access-Control-Allow-Origin", origin);
                        response.setHeader("Access-Control-Allow-Methods", responceconf.getHeader().getMethods());
                        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(responceconf.getHeader().getCredentials()));
                        response.setHeader("Access-Control-Allow-Headers", responceconf.getHeader().getHeaders());
                        break;
                    }
                } else {
                    if (referer.startsWith(origin)) {
                        response.setHeader("Access-Control-Allow-Origin", origin);
                        response.setHeader("Access-Control-Allow-Methods", responceconf.getHeader().getMethods());
                        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(responceconf.getHeader().getCredentials()));
                        response.setHeader("Access-Control-Allow-Headers", responceconf.getHeader().getHeaders());
                        break;
                    }
                }
            }
        }
    }
    @RequestMapping(value = "/{module}/{executor}_jsp",method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView jspCore(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String module,
                                @PathVariable(value = "executor") String executor,
                                @RequestParam(value = "redirecttoken", required = false) String redirecttoken,
                                Map<String,Object> map){
         /* +------------------------- 返回跨域设置处理 -------------------------+ */
        handleResponseHeader(response, request.getHeader("referer"));
        if(redirecttoken != null){
            map.put("inpool",this.getRedirectCache(redirecttoken).get("inpool"));
            map.put("dataBody",this.getRedirectCache(redirecttoken).get("dataBody"));
            map.put("dataHead",this.getRedirectCache(redirecttoken).get("dataHead"));
            return new ModelAndView(((HashMap)(map.get("dataBody"))).get("jsp").toString()) ;
        }else{
            map.put("module",module);
            return new ModelAndView(executor);
        }

    }
}
