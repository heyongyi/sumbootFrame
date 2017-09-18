package org.sumbootFrame.tools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by thinkpad on 2017/9/12.
 */
@ConfigurationProperties(prefix = "sum.cookie")
@PropertySource({
        "classpath:cookie.properties",
        "classpath:static/property/cookie-self.properties"
})
@Component
public class CookieConfig {
    private String domain;
    private String path;
    private boolean httpOnly;
    private boolean secure;
    private int age;

    public String getDomain(){return domain;}
    public void setDomain(String cookieDomain){this.domain = cookieDomain;}
    public String getPath(){return path;}
    public void setPath(String cookiePath){this.path = cookiePath;}
    public boolean getSecure(){return secure;}
    public void setSecure(boolean cookieSecure){this.secure = cookieSecure;}
    public boolean getHttpOnly(){return httpOnly;}
    public void setHttpOnly(boolean cookieHttpOnly){this.httpOnly=cookieHttpOnly;}

    public int getAge(){return age;}
    public void setAge(int age){this.age = age;}
}
