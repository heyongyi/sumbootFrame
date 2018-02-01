package org.sumbootFrame.mvc.controller;

import com.google.code.kaptcha.Producer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.sumbootFrame.data.mao.RedisDao;
import org.sumbootFrame.tools.JugUtil;
import org.sumbootFrame.tools.config.AppConfig;
import org.sumbootFrame.tools.config.AuthorityConfig;
import org.sumbootFrame.tools.config.CookieConfig;
import org.sumbootFrame.tools.config.ResponceConfig;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by thinkpad on 2018/1/24.
 */
@Controller
@Scope("request")
public class CaptchaController {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(CaptchaController.class);
    @Autowired
    ApplicationContext context;
    @Autowired
    AppConfig appconf;
    @Autowired
    ResponceConfig responceconf;
    @Autowired
    CookieConfig cookieconf;
    @Autowired
    AuthorityConfig authorityConfig;
    @Autowired
    private Producer captchaProducer;

    private String authToken;
    private HashMap<String, Object> cookies;
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public String getAuthToken() {return authToken;}
    public HashMap<String, Object> getCookies() {
        return cookies;
    }
    public void setCookies(HashMap<String, Object> cookies) {this.cookies = cookies;}
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
    private void handleCookies(HttpServletRequest request) {//请求中获取cookies
        HashMap<String, Object> cookies = new LinkedHashMap<>();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookies.put(cookie.getName(), cookie.getValue());
                //从Cookies获取令牌标识,并且，url路径无令牌参数
                if (cookie.getName().equals(appconf.getTokenName())) {
                    if (StringUtils.isEmpty(this.getAuthToken())) {
                        this.setAuthToken(cookie.getValue());
                    }
                }
            }
        }
        if(StringUtils.isEmpty(this.getAuthToken())){
            this.setAuthToken(JugUtil.getLongUuid());
        }
        cookies.put(appconf.getTokenName(),this.getAuthToken());
        this.setCookies(cookies);
    }
    private void handleResponseCookies(HttpServletResponse response) {//响应中返回cookies
        for (Map.Entry<String, Object> entry : this.getCookies().entrySet()) {
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
    public HashMap<String, Object> getSessionContext(String sessionField) {
        HashMap<String, Object> sessionContext;
        RedisDao redisDao;
        try {
            redisDao = (RedisDao) context.getBean("RedisDao");
            sessionContext = redisDao.read(appconf.getSessionChannel()+"-"+sessionField,""+sessionField);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if (sessionContext == null) {
            sessionContext = new HashMap<String, Object>();
        }
        return sessionContext;
    }
    public void setSessionContext(HashMap<String, Object> sessionContext,String sessionField) {
        RedisDao redisDao;
        if(sessionField != null){
            try {
                redisDao = (RedisDao) context.getBean(
                        "RedisDao");
                redisDao.save(appconf.getSessionChannel()+"-"+sessionField, sessionField,
                        (Object)sessionContext, appconf.getSessionTimeout());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
    //验证码获取逻辑，验证码缓存至sessionContext
    @RequestMapping(value = "{module}/captcha.image",method = RequestMethod.GET)
    public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response,
                                        @PathVariable String module) throws Exception {     // 请求路径解析
        this.handleCookies(request);
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");

        handleResponseHeader(response, request.getHeader("referer"));

        // create the text for the image
        String capText = captchaProducer.createText();

        // store the text in the session

        this.handleResponseCookies(response);
        HashMap<String, Object> hm = this.getSessionContext(this.getAuthToken());
        hm.put(authorityConfig.getCaptchaKey(), capText);
        this.setSessionContext(hm,this.getAuthToken());
        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }
}
