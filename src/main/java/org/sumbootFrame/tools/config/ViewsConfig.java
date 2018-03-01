package org.sumbootFrame.tools.config;

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
@PropertySource({
        "classpath:self-properties/sum-self.properties"
})
@Component
public class ViewsConfig {
    private Map<String, String> urlRoute;
    private String urlRouteDefault;

    public Map<String, String> getUrlRoute(){
        return urlRoute;
    }
    public void setUrlRoute(Map<String, String> urlRoute){
        this.urlRoute = urlRoute;
    }

    public String getUrlRouteDefault(){
        return urlRouteDefault;
    }
    public void setUrlRouteDefault(String urlRouteDefault){
        this.urlRouteDefault = urlRouteDefault;
    }
}
