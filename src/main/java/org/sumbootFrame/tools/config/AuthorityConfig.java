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

    private String sessionObjName;
    private String sessionObjIdentifyName;
    private String sessionObjRoleName;
    private String sessionObjLimitName;
    private String sessionObjDetailName;
    private String captchaKey;
    private String loginPage;


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

    public String getSessionObjDetailName() {
        return sessionObjDetailName;
    }

    public void setSessionObjDetailName(String sessionObjDetailName) {
        this.sessionObjDetailName = sessionObjDetailName;
    }

    public String getSessionObjLimitName() {
        return sessionObjLimitName;
    }

    public void setSessionObjLimitName(String sessionObjLimitName) {
        this.sessionObjLimitName = sessionObjLimitName;
    }

    public String getSessionObjRoleName() {
        return sessionObjRoleName;
    }

    public void setSessionObjRoleName(String sessionObjRoleName) {
        this.sessionObjRoleName = sessionObjRoleName;
    }

    public String getSessionObjIdentifyName() {
        return sessionObjIdentifyName;
    }

    public void setSessionObjIdentifyName(String sessionObjIdentifyName) {
        this.sessionObjIdentifyName = sessionObjIdentifyName;
    }
}
