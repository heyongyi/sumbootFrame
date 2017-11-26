package org.sumbootFrame.tools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by thinkpad on 2017/9/13.
 */
@ConfigurationProperties(prefix = "sum.auth")
@PropertySource({
        "classpath:properties/sum.properties",
        "classpath:self-properties/sum-self.properties"
})
@Component
public class AuthorityConfig {
    private Map<String, String> sessionObj;
    private String sessionObjName;
    private String captchaKey;
    private String loginPage;

    public Map<String, String> getSessionObj(){
        return sessionObj;
    }
    public void setSessionObj(Map<String, String> sessionObj){this.sessionObj = sessionObj;}
    public String getSessionObjName(){
        return sessionObjName;
    }
    public void setSessionObjName(String sessionObjName){this.sessionObjName = sessionObjName;}
    public String getCaptchaKey(){
        return captchaKey;
    }
    public  void setCaptchaKey(String captchaKey){this.captchaKey = captchaKey;}

    public String getLoginPage(){return loginPage;}
    public void setLoginPage(String loginPage){this.loginPage = loginPage;}
}
