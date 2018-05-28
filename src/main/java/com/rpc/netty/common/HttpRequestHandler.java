package com.rpc.netty.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.cookie.*;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.AsciiString;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.sumbootFrame.data.mao.RedisDao;
import org.sumbootFrame.mvc.interfaces.ServiceInterface;
import org.sumbootFrame.mvc.interfaces.ServiceRpcInterface;
import org.sumbootFrame.tools.*;
import org.sumbootFrame.tools.exception.MyException;
import org.sumbootFrame.tools.mq.PubClientUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
/*
**目前只建议用于写后台能力接口
** 放弃session
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);
	private AsciiString contentType = HttpHeaderValues.TEXT_PLAIN;
//    private String authToken;//登录用户唯一身份标识
    private String serviceTicket;//接口调用权限令牌
    private HashMap<String, Object> cookies;
    private HashMap<String, Object> hmContext = new HashMap<>();
    protected static final HashMap<String,Object> viewsMap = makePropertyConfig("sum.views","self-properties/sum-self.properties");
    protected static final HashMap<String,Object> appMap = makePropertyConfig("sum.app","self-properties/sum-self.properties");
    protected static final HashMap<String,Object> cookieMap = makePropertyConfig("sum.cookie","self-properties/sum-self.properties");
    protected static final HashMap<String,Object> redisClusterMap = makePropertyConfig("spring.redis.cluster","application.properties");
    protected static final HashMap<String,Object> redisMap = makePropertyConfig("spring.redis","application.properties");
    protected static final HashMap<String,Object> responceMap = makePropertyConfig("sum.response.header","self-properties/sum-self.properties");
    protected final HashMap<String,Object> RpcServiceFactory = jedisRead(appMap.get("moduleName")+"Cache"+"-mem","RpcServiceFactory");
//    public void setAuthToken(String authToken) {
//        this.authToken = authToken;
//    }
//    public String getAuthToken() {return authToken;}
    public void setServiceTicket(String serviceTicket){
        this.serviceTicket = serviceTicket;
    }
    public String getServiceTicket(){return serviceTicket;}
    public HashMap<String, Object> getCookies() {
        return cookies;
    }
    public void setCookies(HashMap<String, Object> cookies) {this.cookies = cookies;}
    public HashMap<String, Object> getHmContext(){return hmContext;}
    public void setHmContext(HashMap<String, Object> hmContext){this.hmContext=hmContext;}
    private HashMap<String, Object> result = new HashMap<String, Object>();
    private HashMap<String, Object> header = new HashMap<String, Object>();
    public HashMap getResult() {return result;}
    public HashMap getHeader() {
        return header;
    }
    public void setResult(ReturnUtil retinfo, HashMap dataSet) {
        this.getHeader().put("appName",appMap.get("moduleName"));
        this.getHeader().put("stateCode",retinfo.getStateCode());
        this.getHeader().put("stateMsg",retinfo.getStateDetail().length()>0?retinfo.getStateDetail():retinfo.getStateMsg());
        this.getHeader().put("success",retinfo.getStateCode().equals(ReturnUtil.SUCCESS.getStateCode()));
        this.getResult().put("dataHead",this.getHeader());
        this.getResult().put("dataBody",dataSet);
    }

    private static HashMap<String,Object> makePropertyConfig(String prefix,String inFile){
        HashMap<String,Object> sumMap = new HashMap<>();
        InputStream defin = HttpRequestHandler.class.getClassLoader().getResourceAsStream( "properties/sum.properties" );
        InputStream prein = HttpRequestHandler.class.getClassLoader().getResourceAsStream( inFile );
        try {
            Properties prop =  new  Properties();
            prop.load(defin);
            Set<Object> sumProKeySet=prop.keySet();
            for(Object fullkey:sumProKeySet){
                String skey = (String)fullkey;
                if(skey.startsWith(prefix+".")){
                    String key = skey.split(prefix+".")[1];
                    sumMap.put(key,prop.getProperty(skey).trim());
                }
            }
            defin.close();


            prop =  new  Properties();
            prop.load(prein);
            sumProKeySet=prop.keySet();
            for(Object fullkey:sumProKeySet){
                String skey = (String)fullkey;
                if(skey.startsWith(prefix+".")){
                    String key = skey.split(prefix+".")[1];
                    sumMap.put(key,prop.getProperty(skey).trim());
                }
            }
            prein.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sumMap;
    }

    private String getExecutor(String module, String et) {
        String executor;
        if (StringUtils.isEmpty(et) && module.equals(appMap.get("moduleName"))) {
            executor = (String) viewsMap.get("urlRouteDefault");
        } else if(!StringUtils.isEmpty(et) && module.equals(appMap.get("moduleName"))){
            executor = (String) viewsMap.get("urlRoute["+et+"]");
        }else{
            executor = null;
        }
        return executor;
    }
    private void handleCookies(FullHttpRequest request) {//请求中获取cookies
        HashMap<String, Object> cookieMap = new LinkedHashMap<>();
        Set<Cookie> cookies;
        String value = request.headers().get("Cookie");
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = CookieDecoder.decode(value);
        }
        if (!cookies.isEmpty()) {
            // Reset the cookies if necessary.
            for (Cookie cookie : cookies) {
                logger.info("cookie: " + cookie.value() +"   "+cookie.name()+ "\r\n");
                cookieMap.put(cookie.name(), cookie.value());
                //从Cookies获取令牌标识,并且，url路径无令牌参数
//                if (cookie.name().equals(appMap.get("moduleName")+"Token")) {
//                    if (StringUtils.isEmpty(this.getAuthToken())) {
//                        this.setAuthToken(cookie.value());
//                    }
//                }
                if (cookie.name().equals(appMap.get("moduleName")+"St")) {
                    if (StringUtils.isEmpty(this.getServiceTicket())) {
                        this.setServiceTicket(cookie.value());
                    }
                }
            }
        }
//        if(StringUtils.isEmpty(this.getAuthToken())){
//            this.setAuthToken(JugUtil.getLongUuid());
//            cookieMap.put(appMap.get("moduleName")+"Token",this.getAuthToken());
//        }

        this.setCookies(cookieMap);
    }
    private void handleResponseCookies(HttpHeaders headers) {//响应中返回cookies
        for (Map.Entry<String, Object> entry : this.getCookies().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            io.netty.handler.codec.http.cookie.Cookie cookie = new DefaultCookie(key,value);
            cookie.setDomain(cookieMap.get("domain").toString());
            cookie.setPath(cookieMap.get("path").toString());
            cookie.setHttpOnly(Boolean.parseBoolean(cookieMap.get("httpOnly").toString()));
            cookie.setMaxAge(Long.parseLong(cookieMap.get("age").toString()));
            cookie.setSecure(Boolean.parseBoolean(cookieMap.get("secure").toString()));
            headers.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
        }
    }
    public HashMap<String, Object> jedisRead(String prefix, final String key) {
        ObjectMapper mapper = new ObjectMapper();

        String json;
        if(redisClusterMap.containsKey("nodes")){
            List<String> clusterNodes = Arrays.asList(redisClusterMap.get("nodes").toString().split(","));
            JedisCluster jedis = new JedisClusterUtil().cluster(redisMap.get("password").toString(),
                    (String[])clusterNodes.subList(0,Integer.parseInt(redisClusterMap.get("max-redirects").toString())).toArray(new String[Integer.parseInt(redisClusterMap.get("max-redirects").toString())]));
            json = jedis.hget(prefix,""+key);
        }else{
            logger.info(redisMap.get("host").toString()+"  "+redisMap.get("port").toString()+"   "+redisMap.get("password").toString());
            Jedis jedis = new Jedis(redisMap.get("host").toString(),Integer.parseInt(redisMap.get("port").toString()));
            jedis.auth(redisMap.get("password").toString());
            jedis.select(Integer.parseInt(redisMap.get("database").toString()));
            json = jedis.hget(prefix,""+key);
        }

        String[] jsonMap= json.split("\\{");
        String resultjson = json.replace(jsonMap[0],"");
        logger.info(resultjson);
        if (resultjson != null) {
            try {
                HashMap<String, Object> map = mapper.readValue(resultjson, HashMap.class);
                return map;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void jedisSave(String prefix,final String key, Object param,int timeout) {
        List<String> clusterNodes = Arrays.asList(redisClusterMap.get("nodes").toString().split(","));
        JedisCluster jedis = new JedisClusterUtil().cluster(redisMap.get("password").toString(),
                (String[])clusterNodes.subList(0,Integer.parseInt(redisClusterMap.get("max-redirects").toString())).toArray(new String[Integer.parseInt(redisClusterMap.get("max-redirects").toString())]));

        ObjectMapper mapper = new ObjectMapper();
        String json=null;
        try {
            json =  mapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        jedis.hset(prefix, key, json);
        if(timeout > 0) {
            jedis.expire(prefix, timeout);
        }
    }
    public HashMap<String, Object> getSessionContext(String sessionField) {
        HashMap<String, Object> sessionContext;
        try {
            sessionContext = jedisRead(appMap.get("moduleName")+"Session"+"-"+sessionField,""+sessionField);
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
        if(sessionField != null){
            try {
                jedisSave(appMap.get("moduleName")+"Session"+"-"+sessionField, sessionField,
                        (Object)sessionContext, Integer.parseInt(appMap.get("sessionTimeout").toString()));
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
    private boolean isNeedSessionLimitCheck(String module, String executor) {
        Boolean exclueModules = Boolean.parseBoolean(appMap.get("exclueModules").toString());
        String[] exclueExecuters = appMap.get("exclueExecuters").toString().split(",");
        if (module.equals(appMap.get("moduleName")) && exclueModules) {
            return false;
        }
        if (Arrays.asList(exclueExecuters).contains(executor)) {
            return false;
        }
        return true;
    }
    private void handleResponseHeader(DefaultFullHttpResponse response, String referer) {
        if (referer != null && response.headers().contains(HttpHeaderNames.ORIGIN)) {
            String[] responseHeaderOrigin = response.headers().get(HttpHeaderNames.ORIGIN).split(",");
            for (String origin : responseHeaderOrigin) {
                //1、如果orgin配置为 http://*.hearglobal.com 校验逻辑如下
                //2、比对请求referer 的一级域名部分是否同orgin 的一级域名部分相同。
                //3、如果相同，则将orgin http://* 替换成referer的二级域名部分作为origin返回
                if (origin.startsWith("http://*.")||origin.startsWith("https://*.")) {
                    if (referer.substring(referer.indexOf(".") + 1).startsWith(origin.substring(origin.indexOf(".") + 1))) {
                        //由于origin不支持http://*.hearglobal.com配置，所以将http://* 替换成 referer的二级域名部分。
                        origin = origin.replace(origin.substring(0, origin.indexOf(".")), referer.substring(0, referer.indexOf(".")));
                        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, responceMap.get("methods"));
                        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(responceMap.get("credentials")));
                        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, responceMap.get("headers"));
                        break;
                    }
                } else {
                    if (referer.startsWith(origin)) {
                        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, responceMap.get("methods"));
                        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(responceMap.get("credentials")));
                        response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, responceMap.get("headers"));
                        break;
                    }
                }
            }
        }
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 3
        response.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    }

    private void handleRequestUrl(String queryString, HashMap<String, Object> urlParam) throws UnsupportedEncodingException {

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
//        urlParam.put("remote-ip", getIpAddress(request));
//        urlParam.put("url", request.getRequestURL());
//        urlParam.put("referer", request.getHeader("referer"));
    }
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(!request.uri().contains("?") || !request.uri().contains("/")){
            HashMap<String, Object> errDataSet = new HashMap<String, Object>();
            errDataSet.put("Errmsg", "");
            this.setResult(ReturnUtil.REQUEST_METHODFORMAT_NO_SUPPORT,errDataSet);
            returnResponse(ctx,request);
            return;
        }
		String urlPath = StringUtils.split(request.uri(),"?")[0];
        String module = StringUtils.split(urlPath,"/")[1].split("/")[0];
        String et = StringUtils.split(urlPath,"/")[1].split("/")[1];
        /**
         * 在服务器端打印请求信息
         */
//        logger.info("REQUEST_URI: " + request.uri() );
//        logger.info("\r\n\r\n");
//        for (Map.Entry<String, String> entry : request.headers()) {
//            logger.info("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
//        }
        /* +---------------------初始化数据暂时存储结构----------------------------+ */
        HashMap<String, Object> allbody = new HashMap<String, Object>();
        /* +--------------------------请求路径透明化处理--------------------------+ */
        String executor = getExecutor(module, et);
        /* +------------------------- 获取cookies参数 -------------------------+ */
        handleCookies(request);
        /* +------------------------- 处理请求路径url参数 ----------------------------+ */
        handleRequestUrl(StringUtils.split(request.uri(),"?")[1], allbody);


        Map<String, String> parmMap = new RequestParser(request).parse(); // 将GET, POST所有请求参数转换成Map对象
        for (String key : parmMap.keySet()) {
//            logger.info("PARAM  : " + key + '=' + parmMap.get(key) + "\r\n");
            allbody.put(key,parmMap.get(key));
        }
        allbody.put("executor", executor);
        String dealType = allbody.get("deal-type").toString();
        //权限验证
        if (isNeedSessionLimitCheck(module, executor) && this.getSessionContext(dealType).get(this.getServiceTicket()) == null) {
            /* +-------------------------session相关处理--------------------------+ */
            if (Integer.parseInt(appMap.get("runningMode").toString()) < 3) { //自动赋当前权限 相当于给与全部权限
                HashMap st = new HashMap<String, Object>();
                st.put(this.getServiceTicket(), "true");
                this.setSessionContext(st, dealType);
            }
            if (this.getSessionContext(dealType).get(this.getServiceTicket()).equals("true")) {

            } else {
                /* 没有权限 */
                HashMap<String, Object> errDataSet = new HashMap<String, Object>();
                errDataSet.put("errorDetail", "服务调用者没有此操作权限，操作功能：" + module + "=>" + executor + "=>" +dealType );
                this.setResult(ReturnUtil.REQUEST_METHOD_NO_AUTH, errDataSet);
                returnResponse(ctx,request);
                return;
            }
        }



//        Class c = Class.forName("org.sumbootFrame.mvc.services.rpc."+executor+"Rpc"); //包名为interview
        String classPath = RpcServiceFactory.get(executor+"Rpc").toString();
        Class c = Class.forName(classPath);
        ServiceRpcInterface si = (ServiceRpcInterface)c.newInstance();

        /*-------------------------session和入参打入 service层 -----------------------------+*/
//        hmContext.put("session", this.getSessionContext(this.getAuthToken()));
        hmContext.put("cookies", this.getCookies());
        si.setContext(hmContext);
        //所有的参数都放入inpool了
        si.setinpool(allbody);


        try{
            if(dealType == null){
                this.setResult(si.initface(), si.getoutpool());
            }else if(dealType.startsWith("select")||dealType.startsWith("query")||dealType.startsWith("get")){
                this.setResult(si.queryface(), si.getoutpool());
            }else{
                this.setResult(si.dealface(), si.getoutpool());
            }
        } catch (MyException e) {
            this.setResult(e.getRet(), si.getoutpool());
        }
        /*-------------------------请求最后保存session -----------------------------+*/
//        this.setSessionContext((HashMap<String, Object>) si.getContext().get("session"),this.getAuthToken());

        returnResponse(ctx,request);
	}

    private void returnResponse(ChannelHandlerContext ctx,FullHttpRequest request){
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(PojoUtil.toJson(this.getResult()).getBytes())); // 2
        response.headers().remove(HttpHeaderNames.CONTENT_TYPE);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"application/json; charset=UTF-8");
        handleResponseHeader(response, request.headers().get("referer"));
        handleResponseCookies(response.headers());
        ctx.write(response);
    }
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelReadComplete");
		super.channelReadComplete(ctx);
		ctx.flush(); // 4
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
