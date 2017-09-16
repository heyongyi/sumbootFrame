package org.sumboot.sumFrame.tools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by thinkpad on 2017/9/13.
 */
//@Configuration
@ConfigurationProperties(prefix = "sum.views")
@PropertySource("classpath:views.properties")
@Component
public class ViewsConfig {
    private Map<String, String> urlRoute;
    private Map<String, String> urlRouteDefault;

    public Map<String, String> getUrlRoute(){
        return urlRoute;
    }
    public void setUrlRoute(Map<String, String> urlRoute){
        this.urlRoute = urlRoute;
    }
    public Map<String, String> getUrlRouteDefault(){
        return urlRouteDefault;
    }
    public void setUrlRouteDefault(Map<String, String> urlRouteDefault){
        this.urlRouteDefault = urlRouteDefault;
    }
}
