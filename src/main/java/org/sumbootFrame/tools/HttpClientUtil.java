package org.sumbootFrame.tools;

import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thinkpad on 2017/11/3.
 */
public class HttpClientUtil {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    private static CookieStore cookiestore;

    private static JsonObject defaultResponse = new JsonObject();

    /** GBK编码 */
    private static final String GBK = "GBK";

    /** UTF-8编码 */
    private static final String UTF8 = "UTF-8";
    private static final int DEFAULT_SOCKET_TIMEOUT = 30000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    private static RequestConfig config = RequestConfig.custom().setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).setConnectTimeout(DEFAULT_CONNECT_TIMEOUT).build();

    public static JsonObject getDefaultResponse(String URL){
        JsonObject dataHead = new JsonObject();
        JsonObject dataBody = new JsonObject();

        dataHead.addProperty("stateCode","00001");
        dataHead.addProperty("stateMsg","客户端访问失败");
        dataHead.addProperty("appName",URL.split("/")[3]);
        dataHead.addProperty("success",false);
        defaultResponse.add("dataHead",dataHead);

        defaultResponse.add("dataBody",dataBody);

        return defaultResponse;
    }
    public static Map httprequest(String URL, Map bodymap, String paramtype) throws Exception {
        cookiestore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookiestore).build();


        Map<String,Object> responsemap = new HashMap<>();
        if(paramtype.equals("json")){
            String jsonString = PojoUtil.toJson(bodymap);

            HttpPost request = new HttpPost(URL);
            try {request.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                request.setConfig(config);
                StringEntity se = new StringEntity(jsonString,HTTP.UTF_8);
                se.setContentType(CONTENT_TYPE_TEXT_JSON);
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
                request.setEntity(se);
                HttpResponse response = httpclient.execute(request);
                responsemap.put("httpcode",response.getStatusLine().getStatusCode());
                responsemap.put("httpbody", EntityUtils.toString(response.getEntity(),HTTP.UTF_8));
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                responsemap.put("httpcode","500");
                responsemap.put("httpbody", getDefaultResponse(URL));
                request.abort();
            } catch (IOException e) {
                e.printStackTrace();
                responsemap.put("httpcode","500");
                responsemap.put("httpbody", getDefaultResponse(URL));
                request.abort();
            }
        }
        return responsemap;
    }

}
