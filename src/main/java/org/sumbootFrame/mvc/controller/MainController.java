package org.sumbootFrame.mvc.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.sumbootFrame.data.mao.RedisDao;
import org.sumbootFrame.mvc.interfaces.ServiceInterface;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.tools.JugUtil;
import org.sumbootFrame.tools.config.*;
import org.sumbootFrame.tools.exception.MyException;

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
@Scope("request")
public class MainController {
    protected Logger logger = Logger.getLogger(this.getClass());
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
    private String serviceTicket;
    private HashMap<String, Object> cookies;
    private HashMap<String, Object> result = new HashMap<String, Object>();
    private HashMap<String, Object> header = new HashMap<String, Object>();
    private HashMap<String, Object> hmContext = new HashMap<>();
    private HashMap<String, Object> hmPagedata = new HashMap<>();
    public void setServiceTicket(String serviceTicket){
        this.serviceTicket = serviceTicket;
    }
    public String getServiceTicket(){return serviceTicket;}
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
        this.getHeader().put("stateMsg",retinfo.getStateDetail().length()>0?retinfo.getStateDetail():retinfo.getStateMsg());
        this.getHeader().put("success",retinfo.getStateCode().equals(ReturnUtil.SUCCESS.getStateCode()));
        this.getResult().put("dataHead",this.getHeader());
        this.getResult().put("dataBody",dataSet);
    }
    public HashMap<String, Object> getHmContext(){return hmContext;}
    public HashMap<String, Object> getHmPagedata(){return hmPagedata;}
    public void setHmContext(HashMap<String, Object> hmContext){this.hmContext=hmContext;}
    public void setHmPagedata(HashMap<String, Object> hmPagedata){this.hmPagedata = hmPagedata;}

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
            sessionContext = new HashMap<>();
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
    public void setRedirectCache(HashMap<String, Object> param, String cacheToken) {
        RedisDao redisDao;
        if(cacheToken != null){
            try {
                redisDao = (RedisDao) context.getBean("RedisDao");
                redisDao.save(appconf.getCacheChanel()+"-"+cacheToken, ""+cacheToken, (Object)param, appconf.getCacheTimeout());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
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
    private boolean isNeedSessionLimitCheck(String module, String executor) {
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
    private boolean isNeedSessionLoginCheck(String module, String executor) {
        String[] exclueModules = appconf.getExclueLoginModules().split(",");
        String[] exclueExecuters = appconf.getExclueLoginExecuters().split(",");
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
        } else if(!StringUtils.isEmpty(et) && viewsconf.getUrlRouteDefault().containsKey(module)){
            executor = (String) viewsconf.getUrlRoute().get(et);
        }else{
            executor = null;
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
                if (cookie.getName().equals("st")) {
                    if (StringUtils.isEmpty(this.getServiceTicket())) {
                        this.setServiceTicket(cookie.getValue());
                    }
                }
            }
        }
        if(StringUtils.isEmpty(this.getAuthToken())){
            this.setAuthToken(JugUtil.getLongUuid());
            cookies.put(appconf.getTokenName(),this.getAuthToken());
        }
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
                                        @RequestParam(value = "ct", required = false) String redirectToken,
                                        @RequestParam(value = "upload-file", required = false) MultipartFile[] uploadFile,  // 文件上传参数
                                        @RequestParam(value = "fileName",required = false)String[] fileName,
                                        @RequestParam(value = "deal-type",required = true)String dealType)throws Exception {

        return coredefault(request,response,module,executor,serviceTicket,redirectToken,uploadFile,fileName,dealType);
    }
    @RequestMapping(value = "/{module}",method = {RequestMethod.POST, RequestMethod.GET})
    public HashMap<String, Object> coredefault
            (HttpServletRequest request,
             HttpServletResponse response,
             @PathVariable String module,
             @RequestParam(value = "et", required = false) String et,
             @RequestParam(value = "st", required = false) String serviceTicket,
             @RequestParam(value = "ct", required = false) String redirectToken ,                   // 请求参数缓存令牌
             @RequestParam(value = "upload-file", required = false) MultipartFile[] uploadFile,  // 文件上传参数
             @RequestParam(value = "file-name",required = false)String[] fileName,
             @RequestParam(value = "deal-type",required = true)String dealType)throws Exception {
        this.setAuthToken(null);//令牌来自Cookies
        this.setServiceTicket(serviceTicket);
        if( Integer.parseInt(appconf.getRunningMode())<3){//测试阶段随机分配st 并会在后面自动复权
            this.setServiceTicket(JugUtil.getLongUuid());
        }

        /* +---------------------初始化数据暂时存储结构----------------------------+ */
        HashMap<String, Object> urldata = new HashMap<String, Object>();
        HashMap<String, Object> formdata = new HashMap<String, Object>();
        HashMap<String, Object> jsonbody = new HashMap<String, Object>();//参数的三个来源
        /* +--------------------------请求路径透明化处理--------------------------+ */
        String executor = getExecutor(module, et);
        if (StringUtils.isEmpty(executor)) {
            HashMap<String,Object> errDataSet=new HashMap<String,Object>();
            errDataSet.put("Errmsg","executor is null");
            this.setResult(ReturnUtil.METHOD_ERROR, errDataSet);
            handleResponseHeader(response, request.getHeader("referer"));
            return this.getResult();
        }
        /* +------------------------- 获取cookies参数 -------------------------+ */
        handleCookies(request);
        /*-------------------------初始化请求路径通用参------------------------+*/
//      urldata.put("authToken", this.getAuthToken());
        urldata.put("module",module);
        urldata.put("executor",et);
        urldata.put("deal-type",dealType);
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
            this.setResult(ReturnUtil.REQUEST_PARAM_FORMAT_ERROR,errDataSet);
            handleResponseHeader(response, request.getHeader("referer"));
            return this.getResult();
        }
        /* +-------------------------处理请求中的form数据 排除url中的参数-------------------+ */
        handleCommonFormData(request, urldata, formdata);//获取@RequestPart的formdata参数  单元测试中的params参数也在此
        /* +-----------------------formdata和jsonbody 放入HmPagedata---------------------+ */
        if(redirectToken == null){
            conformHmPagedata(formdata,jsonbody);
        }else{
            this.setHmPagedata(this.getRedirectCache(redirectToken));
        }
        /*-------------------------上传文件参数传递给业务层--------------------+*/
        if (uploadFile != null) {
            this.getHmPagedata().put("uploadFile", uploadFile);
            this.getHmPagedata().put("fileName",fileName);
        }


        // SESSION登陆验证，统一验证的方式有待在考虑
        //1.所有的前台接口都不需要权限验证，但需要登录验证
        //2.所有的后台能力都不需要登录验证，但需要权限验证
        //3.权限验证需要确定 调用方 提供方 能力 三个条件
        if (isNeedSessionLoginCheck(module, executor) && this.getSessionContext(this.getAuthToken()).get(authorityConfig.getSessionObjName()) == null){
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
                String reToken = JugUtil.getLongUuid();//随机生成
                this.setRedirectCache(requestParam, reToken);
                redirectUrlParam = URLEncoder.encode(requestURL + "?ct=" + reToken, "UTF-8");
            }
            response.sendRedirect(authorityConfig.getLoginPage() + "?redirect-url=" + redirectUrlParam);
            return this.getResult();
        }else if(isNeedSessionLimitCheck(module, executor) && this.getSessionContext(dealType).get(this.getServiceTicket()) == null){

            /* +-------------------------session相关处理--------------------------+ */
            if( Integer.parseInt(appconf.getRunningMode())<3){ //自动赋当前权限 相当于给与全部权限
                HashMap st = new HashMap<String, Object>();
                st.put(this.getServiceTicket(),"true");
                this.setSessionContext(st,dealType);
            }
            if( this.getSessionContext(dealType).get(this.getServiceTicket()).equals("true")){

            } else {
                /* 没有权限 */
                HashMap<String,Object> errDataSet=new HashMap<String,Object>();
                errDataSet.put("Errmsg","服务调用者没有此操作权限，操作功能："+module+"=>"+executor+"=>"+dealType);
                this.setResult(ReturnUtil.REQUEST_METHOD_NO_AUTH,errDataSet);
                handleResponseHeader(response, request.getHeader("referer"));
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
            this.setResult(ReturnUtil.METHOD_ERROR, errDataSet);
            handleResponseHeader(response, request.getHeader("referer"));
            return this.getResult();
        }
        /* +-------------------------处理用户缓存区的cache数据-------------------+ */
        /*-------------------------session和入参打入 service层 -----------------------------+*/
        hmContext.put("session", this.getSessionContext(this.getAuthToken()));
        hmContext.put("cookies", this.getCookies());
        si.setContext(hmContext);
        //所有的参数都放入inpool了
        si.setinpool(this.getHmPagedata());

        Iterator urldataIt = urldata.keySet().iterator();
        while(urldataIt.hasNext()) {
            String param1 = (String)urldataIt.next();
            si.getinpool().put(param1, urldata.get(param1));
        }
        /*-------------------------执行 service bean并返回结果 -----------------------------+*/
        try {
            if(dealType.startsWith("select")||dealType.startsWith("query")||dealType.startsWith("get")){
                this.setResult(si.queryface(), si.getoutpool());
            }else{
                this.setResult(si.dealface(), si.getoutpool());
            }
        } catch (MyException e) {
            this.setResult(e.getRet(), si.getoutpool());
        } catch (Exception e) {
            if(Integer.parseInt(this.appconf.getRunningMode()) < 2) {
                this.logger.debug("SUM boot=>", e);
            }
            this.setResult(ReturnUtil.THROW_ERROR, si.getoutpool());
        }

        /*------------------------- 请求最后保存session -----------------------------+*/
        this.setSessionContext((HashMap<String, Object>) si.getContext().get("session"),this.getAuthToken());
        /*--------------------------------------------------------------------------*/


        if(!StringUtils.isEmpty(si.getoutpool().get("jsp"))){
            String redirecttoken = JugUtil.getLongUuid();//随机生成
//            response.sendRedirect("/"+module+"/"+et+"_jsp"+"?redirecttoken="+redirecttoken);
            request.getRequestDispatcher("/"+module+"/"+et+"_jsp"+"?redirecttoken="+redirecttoken).forward(request, response);
            HashMap param = new HashMap();
            param.put("inpool", si.getinpool());
            param.put("dataBody", si.getoutpool());
            param.put("dataHead", this.getHeader());
            this.setRedirectCache(param, redirecttoken);
            return this.getResult();
        }
        // 文件下载：判断业务逻辑层是否存在downLoadPath，fileName变量
        else if (!StringUtils.isEmpty(si.getoutpool().get("downLoadPath")) && !StringUtils.isEmpty(si.getoutpool().get("fileName"))) {
            request.getRequestDispatcher("/"+module+"/download?dp="+ si.getoutpool().get("downLoadPath")+"&fn=" + si.getoutpool().get("fileName")).forward(request, response);
            return this.getResult();
        }
        else{
            /* +------------------------- 返回cookies处理 -------------------------+ */
            handleResponseCookies(response);
            /* +------------------------- 返回跨域设置处理 -------------------------+ */
            handleResponseHeader(response, request.getHeader("referer"));

            hmPagedata.remove("uploadFile");
            return this.getResult();
        }

    }

}
