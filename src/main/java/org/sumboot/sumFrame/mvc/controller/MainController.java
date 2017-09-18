package org.sumboot.sumFrame.mvc.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.sumboot.sumFrame.data.mao.RedisDao;
import org.sumboot.sumFrame.mvc.interfaces.ServiceInterface;
import org.sumboot.sumFrame.tools.config.*;
import org.sumboot.sumFrame.utils.RETURN;
import org.sumboot.sumFrame.tools.ReturnUtil;
import org.sumboot.sumFrame.tools.JugUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by thinkpad on 2017/9/11.
 */

@RestController
public class MainController {
    @Autowired
    ApplicationContext context;
    @Autowired
    AppConfig appconf;
    @Autowired
    CookieConfig cookieconf;
    @Autowired
    ResponceConfig responceconf;
    @Autowired
    ViewsConfig viewsconf;
    @Autowired
    AuthorityConfig authorityConfig;

    private String authToken;
    private HashMap<String, Object> cookies;
    private HashMap<String, Object> result = new HashMap<String, Object>();
    private HashMap<String, Object> header = new HashMap<String, Object>();
    private HashMap<String, Object> hmContext = new HashMap<>();
    private HashMap<String, Object> hmPagedata = new HashMap<>();

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public String getAuthToken() {return authToken;}
    public HashMap<String, Object> getCookies() {
        return cookies;
    }
    public void setCookies(HashMap<String, Object> cookies) {this.cookies = cookies;}
    public HashMap getResult() {return result;}
    public HashMap getHeader() {
        return header;
    }
    public void setResult(ReturnUtil retinfo, HashMap dataSet) {
        this.getHeader().put("appName",appconf.getName());
        this.getHeader().put("stateCode",retinfo.getStateCode());
        this.getHeader().put("stateMsg",retinfo.getStateMsg());
        this.getHeader().put("success",retinfo.getStateCode().equals("00000"));
        this.getResult().put("header",this.getHeader());
        this.getResult().put("dataSet",dataSet);
    }
    public HashMap<String, Object> getHmContext(){return hmContext;}
    public HashMap<String, Object> getHmPagedata(){return hmContext;}
    public void setHmContext(HashMap<String, Object> hmContext){this.hmContext=hmContext;}
    public void setHmPagedata(HashMap<String, Object> hmPagedata){this.hmPagedata = hmPagedata;}

    public HashMap<String, Object> getSessionContext(String sessionField) {
        HashMap<String, Object> sessionContext;
        RedisDao redisDao;
        try {
            redisDao = (RedisDao) context.getBean("RedisDao");
            sessionContext = redisDao.read(appconf.getSessionChannel()+"-"+this.getAuthToken(),sessionField);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        if (sessionContext == null) {
            sessionContext = new HashMap<>();
        }
        return sessionContext;
    }

    public void setSessionContext(HashMap<String, Object> sessionContext,String sessionField) {
        RedisDao redisDao;
        try {
            redisDao = (RedisDao) context.getBean(
                    "RedisDao");
//            System.out.println(appconf.getSessionChannel()+"--"+this.getAuthToken()+"--"+sessionContext);
            redisDao.save(appconf.getSessionChannel()+"-"+this.getAuthToken(), sessionField,
                    (Object)sessionContext, appconf.getSessionTimeout());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     *
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     *
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if("0:0:0:0:0:0:0:1".equals(ip)){
            ip = "localhost";
        }
        return ip;
    }
    private void handleRequestUrl(HttpServletRequest request, HashMap<String, Object> urlParam) throws UnsupportedEncodingException {
        String queryString = request.getQueryString();
        if (!StringUtils.isEmpty(queryString)) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx < 0) {// http:xxx.com/aa?pp
                    String key = URLDecoder.decode(pair.substring(0), "UTF-8");
                    String value = null;
                    if (!urlParam.containsKey(key)) {
                        urlParam.put(key, value);
                    }
                } else {
                    String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                    String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                    if (!urlParam.containsKey(key)) {
                        urlParam.put(key, value);
                    }
                }

            }
        }
        urlParam.put("remote-ip", getIpAddress(request));
        urlParam.put("url", request.getRequestURL());
        urlParam.put("referer", request.getHeader("referer"));
    }
    private HashMap<String, Object> getJsonFormData(HttpServletRequest request) throws IOException {
        HashMap<String, Object> pageData = null;
        String jsonData = IOUtils.toString(request.getInputStream(),"UTF-8");
        if (jsonData != null && jsonData.length() > 0) {
            JsonFactory factory = new JsonFactory();
            ObjectMapper mapper = new ObjectMapper(factory);
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };
            HashMap<String, Object> jsonHm = mapper.readValue(jsonData, typeRef);
            pageData=jsonHm;
        }
        return pageData;
    }
    private void handleCommonFormData(HttpServletRequest request, HashMap<String, Object> urlParam, HashMap<String, Object> pageData) {
        Map<String, String[]> map = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            if (!urlParam.containsKey(entry.getKey())) {

                /* fd['']包含的变量为spring 标签 form变量<form:input/> */
                if ((!entry.getKey().startsWith("fd['"))
                        && (!entry.getKey().endsWith("']"))) {
                    String[] values = entry.getValue();
                    if (values.length <= 1) {
                        pageData.put(entry.getKey(), values[0]);
                    } else {
                        pageData.put(entry.getKey(), values);
                    }
                }
            }
        }
    }
    public HashMap<String, Object> getCache(String cacheToken) {
        HashMap<String, Object> cachedParam;
        RedisDao redisDao;
        try {
            redisDao = (RedisDao) context.getBean("RedisDao");
            cachedParam = redisDao.read(appconf.getCacheChanel(), cacheToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return cachedParam;
    }
    public void setCache(HashMap<String, Object> param, String cacheToken) {
        RedisDao redisDao;
        try {
            redisDao = (RedisDao) context.getBean("RedisDao");
            redisDao.save(appconf.getCacheChanel(), cacheToken, (Object)param, appconf.getCacheTimeout());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void conformHmPagedata(HashMap<String, Object> formdata,HashMap<String, Object> jsonbody){
        for(String key:formdata.keySet()){
            this.getHmPagedata().put(key,formdata.get(key));
        }
        for(String key:jsonbody.keySet()){
            this.getHmPagedata().put(key,jsonbody.get(key));
        }
    }
    private boolean isNeedSessionCheck(String module, String executor) {
        String[] exclueModules = appconf.getExclueModules().split(",");
        String[] exclueExecuters = appconf.getExclueExecuters().split(",");
        if (Arrays.asList(exclueModules).contains(module)) {
            return false;
        }
        if (Arrays.asList(exclueExecuters).contains(executor)) {
            return false;
        }
        return true;
    }
    private String getExecutor(String module, String et) {
        String executor;
        if (StringUtils.isEmpty(et)) {
            executor = (String) viewsconf.getUrlRouteDefault().get(module);
        } else {
            executor = (String) viewsconf.getUrlRoute().get(et);
        }
        if (executor == null) {
            executor = et;
        }
        return executor;
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
        cookies.put(appconf.getTokenName(), this.getAuthToken());
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
    @RequestMapping(value = "/{module}/{executor}",method = {RequestMethod.POST, RequestMethod.GET})
    public HashMap<String, Object> core(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @PathVariable String module,
                                        @PathVariable(value = "executor") String executor,
                                        @RequestParam(value = "st", required = false) String serviceTicket,
                                        @RequestParam(value = "ct", required = false) String cacheToken,
                                        @RequestParam(value = "upload-file", required = false) MultipartFile[] uploadFile,  // 文件上传参数
                                        @RequestParam(value = "fileName",required = false)String[] fileName)throws Exception {

        return coredefault(request,response,module,executor,serviceTicket,cacheToken,uploadFile,fileName);
    }
    @RequestMapping(value = "/{module}",method = {RequestMethod.POST, RequestMethod.GET})
    public HashMap<String, Object> coredefault
            (HttpServletRequest request,
             HttpServletResponse response,
             @PathVariable String module,
             @RequestParam(value = "et", required = false) String et,
             @RequestParam(value = "st", required = false) String serviceTicket,
             @RequestParam(value = "ct", required = false) String cacheToken ,                   // 请求参数缓存令牌
             @RequestParam(value = "upload-file", required = false) MultipartFile[] uploadFile,  // 文件上传参数
             @RequestParam(value = "fileName",required = false)String[] fileName)throws Exception {
        this.setAuthToken(null);//令牌来自Cookies

        /* +---------------------初始化数据暂时存储结构----------------------------+ */
        HashMap<String, Object> urldata = new HashMap<>();
        HashMap<String, Object> formdata = new HashMap<>();
        HashMap<String, Object> jsonbody = new HashMap<>();//参数的三个来源
        /* +--------------------------请求路径透明化处理--------------------------+ */
        String executor = getExecutor(module, et);
        if (StringUtils.isEmpty(executor)) {
            HashMap<String,Object> errDataSet=new HashMap<String,Object>();
            errDataSet.put("Errmsg","executor is null");
            this.setResult(RETURN.METHOD_ERROR, errDataSet);
            handleResponseHeader(response, request.getHeader("referer"));
            return this.getResult();
        }
        /* +------------------------- 获取cookies参数 -------------------------+ */
        handleCookies(request);
        /*-------------------------初始化请求路径通用参------------------------+*/
//      urldata.put("authToken", this.getAuthToken());
        /* +------------------------- 处理请求路径url参数 ----------------------------+ */
        handleRequestUrl(request, urldata);
        try {
        /* +-------------------------处理请求体中的json数据-------------------+ */
            if (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").contains("application/json")) {
                jsonbody = getJsonFormData(request);//获取@RequestBody 的 json参数
            }
        } catch (Exception e) {
                /* 参数格式化异常捕捉 */
            HashMap<String,Object> errDataSet=new HashMap<String,Object>();
            errDataSet.put("Errmsg",e.getMessage());
            this.setResult(RETURN.REQUEST_PARAM_FORMAT_ERROR,errDataSet);
            handleResponseHeader(response, request.getHeader("referer"));
            return this.getResult();
        }
        /* +-------------------------处理请求中的form数据-------------------+ */
        handleCommonFormData(request, urldata, formdata);//获取@RequestPart 的 formdata参数
        if(cacheToken == null){
            conformHmPagedata(formdata,jsonbody);
        }else{
            this.setHmPagedata(this.getCache(cacheToken));
        }
        /*-------------------------上传文件参数传递给业务层--------------------+*/
        if (uploadFile != null) {
            this.getHmPagedata().put("uploadFile", uploadFile);
            this.getHmPagedata().put("fileName",fileName);
        }
        /* +-------------------------session相关处理--------------------------+ */
        // SESSION登陆验证，统一验证的方式有待在考虑
        //1.判断是否需要验证
        //2.判断session中是否有ST（根据autotoken）
        //3.请求cas获取ST （服务提供方唯一标识+服务调用方唯一标识(autotoken)，需要鉴权方法名(excuter/dealtype)，ST）
        // 3.1，CAS验证时没有autotoken，CAS跳转到登录页,登陆成功后分配autotoken并记录autotoken与serviceticken的对应关系
        // 3.2，CAS验证时有autoToken，查询autotoken与serviceticken的对应关系
        //  3.2.1，查到对应关系(分别从缓存和数据库中查找：CAS应该提供两种autotoken机制（自动过期的和永久的）)
        //  3.2.2，没查到CAS跳转到登录页,登陆成功后分配autotoken并记录autotoken与serviceticken的对应关系
        //4.获取到对应关系后应当入服务提供方session
        if (isNeedSessionCheck(module, executor)){
            if (this.getSessionContext(executor).size() == 0) {//无令牌（ST） 可以做到dealType级别
                String requestURL;
                if (request.getQueryString() != null) {
                    requestURL = request.getRequestURL() + "?" + request.getQueryString();
                } else {
                    requestURL = request.getRequestURL().toString();
                }
                String redirectUrlParam = null;
                if (request.getMethod().equals("GET")) {
                    redirectUrlParam = URLEncoder.encode(requestURL, "UTF-8");
                } else if (request.getMethod().equals("POST")) {
                    HashMap requestParam = new HashMap();
                    requestParam.put("PageData", this.getHmPagedata());
                    String cachToken = JugUtil.getLongUuid();//随机生成
                    this.setCache(requestParam, cachToken);
                    redirectUrlParam = URLEncoder.encode(requestURL + "?ct=" + cachToken, "UTF-8");
                }
                response.sendRedirect(authorityConfig.getLoginPage() + "?redirect-url=" + redirectUrlParam);
                return this.getResult();
            }
        }
        /*-------------------------获取执行者bean -----------------------------+*/
        ServiceInterface si;
        try {
            si= (ServiceInterface)context.getBean(executor);
        } catch (NoSuchBeanDefinitionException e) {
            HashMap<String,Object> errDataSet=new HashMap<String,Object>();
            errDataSet.put("Errmsg","NoSuchBeanDefinitionException");
            this.setResult(RETURN.METHOD_ERROR, errDataSet);
            handleResponseHeader(response, request.getHeader("referer"));
            return this.getResult();
        }
        /* +-------------------------处理用户缓存区的cache数据-------------------+ */
        /*-------------------------session和入参打入 service层 -----------------------------+*/
        hmContext.put("session", this.getSessionContext(this.getAuthToken()));
        hmContext.put("cache", this.getCache(this.getAuthToken()));
        si.setContext(hmContext);
        //所有的参数都放入inpool了
        si.setinpool(this.getHmPagedata());
        for(String key:urldata.keySet()){
            si.getinpool().put(key,urldata.get(key));
        }
        /*-------------------------执行 service bean并返回结果 -----------------------------+*/

        this.setResult(si.dealface(), si.getoutpool());
//        System.out.println(this.getAuthToken());

        /*-------------------------请求最后保存session -----------------------------+*/
        this.setSessionContext((HashMap<String, Object>) si.getContext().get("session"),this.getAuthToken());
        /*-------------------------请求最后保存cache -----------------------------+*/
        this.setCache((HashMap<String, Object>) si.getContext().get("cache"),this.getAuthToken());
        /* +------------------------- 返回cookies处理 -------------------------+ */
        handleResponseCookies(response);
        /* +------------------------- 返回跨域设置处理 -------------------------+ */
        handleResponseHeader(response, request.getHeader("referer"));
        // 文件下载：判断业务逻辑层是否存在downLoadPath，fileName变量
        if (!StringUtils.isEmpty(si.getoutpool().get("downLoadPath")) && !StringUtils.isEmpty(si.getoutpool().get("fileName"))) {
            request.getRequestDispatcher("/"+module+"/download?dp="+ si.getoutpool().get("downLoadPath")+"&fn=" + si.getoutpool().get("fileName")).forward(request, response);
            return this.getResult();
        }
        hmPagedata.remove("uploadFile");
        return this.getResult();
    }

}
