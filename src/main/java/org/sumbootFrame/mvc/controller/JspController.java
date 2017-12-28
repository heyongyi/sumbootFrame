package org.sumbootFrame.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.sumbootFrame.data.mao.RedisDao;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.tools.config.AppConfig;
import org.sumbootFrame.tools.config.CookieConfig;
import org.sumbootFrame.tools.config.ResponceConfig;
import org.sumbootFrame.tools.config.ViewsConfig;

import javax.servlet.http.Cookie;
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
    @Autowired
    ViewsConfig viewsconf;
    @Autowired
    CookieConfig cookieconf;
    public HashMap<String, Object> getRedirectCache(String cacheToken) {
        HashMap<String, Object> cachedParam;
        RedisDao redisDao;
        try {
            redisDao = (RedisDao) context.getBean("RedisDao");
            cachedParam = redisDao.read(appconf.getCacheChanel()+"-"+cacheToken, ""+cacheToken);
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
    private HashMap<String, Object> header = new HashMap<String, Object>();
    private HashMap<String, Object> result = new HashMap<String, Object>();
    public HashMap getResult() {return result;}
    public HashMap getHeader() {
        return header;
    }
    public void setResult(ReturnUtil retinfo, HashMap dataSet) {
        this.getHeader().put("appName",appconf.getName());
        this.getHeader().put("stateCode",retinfo.getStateCode());
        this.getHeader().put("stateMsg",retinfo.getStateDetail().length()>0?retinfo.getStateDetail():retinfo.getStateMsg());
        this.getHeader().put("success",retinfo.getStateCode().equals(ReturnUtil.SUCCESS.getStateCode()));
        this.getResult().put("dataHead",this.getHeader());
        this.getResult().put("dataBody",dataSet);
    }
    private String getExecutor(String module) {
        String executor;
        if(module != null && viewsconf.getUrlRouteDefault().containsKey(module)){
            executor = (String) viewsconf.getUrlRouteDefault().get(module);
        }else{
            executor = null;
        }
        return executor;
    }
    private void handleResponseCookies(HttpServletResponse response,HashMap<String,Object> cookies) {//响应中返回cookies
        for (Map.Entry<String, Object> entry : cookies.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            Cookie cookie = new Cookie(key, value);
            if (!StringUtils.isEmpty(cookieconf.getDomain())) {
                cookie.setDomain(cookieconf.getDomain());
            }
            cookie.setHttpOnly(cookieconf.getHttpOnly());
            cookie.setSecure(cookieconf.getSecure());
            cookie.setPath(cookieconf.getPath());
            cookie.setMaxAge(cookieconf.getAge());
            response.addCookie(cookie);
        }
    }
    @RequestMapping(value = "/{module}/{executor}_jsp",method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView jspCore(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable(value = "module") String module,
                                @PathVariable(value = "executor") String executor,
                                @RequestParam(value = "redirecttoken", required = false) String redirecttoken
                                ){
         /* +------------------------- 返回跨域设置处理 -------------------------+ */
        handleResponseHeader(response, request.getHeader("referer"));

        ModelAndView view = new ModelAndView(executor);
        if(this.getExecutor(module) == null){
            HashMap<String,Object> errDataSet=new HashMap<String,Object>();
            errDataSet.put("Errmsg","module不正确");
            this.setResult(ReturnUtil.METHOD_ERROR,errDataSet);
            view.addObject(this.getResult());
            view.setViewName(null);
            return view;
        }
        if(redirecttoken != null){
            handleResponseCookies(response,(HashMap<String,Object>)this.getRedirectCache(redirecttoken).get("cookies"));
            view = new ModelAndView(((HashMap)(this.getRedirectCache(redirecttoken).get("dataBody"))).get("jsp").toString());
            view.addObject("inpool",this.getRedirectCache(redirecttoken).get("inpool"));
            view.addObject("dataBody",this.getRedirectCache(redirecttoken).get("dataBody"));
            view.addObject("dataHead",this.getRedirectCache(redirecttoken).get("dataHead"));
            return  view;
        }else{
            view.addObject("module",module);
            view.setViewName(executor);
            return view;
        }

    }
    @RequestMapping(value = "/{module}/{way}/{executor}_jsp",method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView jspdefaultCore(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable(value = "module") String module,
            @PathVariable(value = "way") String way,
            @PathVariable(value = "executor") String executor,
            Map<String,Object> map){
         /* +------------------------- 返回跨域设置处理 -------------------------+ */

        ModelAndView view = new ModelAndView();
        if(this.getExecutor(module) != null){
            handleResponseHeader(response, request.getHeader("referer"));
            map.put("module",module);
            map.put("path",way);
            view.setViewName(way+"/"+executor);
            return view;
        }else{
            HashMap<String,Object> errDataSet=new HashMap<String,Object>();
            errDataSet.put("Errmsg","module不正确");
            this.setResult(ReturnUtil.METHOD_ERROR,errDataSet);
            view.addObject( this.getResult());
            view.setViewName(null);
            return view;
        }

    }
}
