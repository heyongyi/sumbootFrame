package org.sumbootFrame.tools.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by thinkpad on 2017/9/12.
 */

@ConfigurationProperties(prefix = "sum.app")
@PropertySource({
        "classpath:properties/sum.properties",
        "classpath:self-properties/sum-self.properties"
})
@Component
public class AppConfig {
    private String name ;
    private String tokenName;
    private String sessionChannel;
    private String cacheChanel;
    private long sessionTimeout;
    private long cacheTimeout;
    private String sysdateSql;
    private int pageSize;
    private String exclueModules;
    private String exclueExecuters;
    private String exclueLoginModules;
    private String exclueLoginExecuters;
    private String runningMode;
    private String contextPath;

    public String getContextPath(){return contextPath ;}
    public void setContextPath(String contextPath){this.contextPath=contextPath;}

    public String getName(){return name ;}
    public void setName(String name){this.name=name;}

    public String getTokenName(){return name+"Token";}
    public String getSessionChannel(){return name+"Session";}
    public String getCacheChanel(){return name+"Cache";}

    public long getSessionTimeout(){return sessionTimeout;}
    public void setSessionTimeout(long sessionTimeout){this.sessionTimeout = sessionTimeout;}
    public long getCacheTimeout(){return cacheTimeout;}
    public void setCacheTimeout(long requestCacheTimeout){this.cacheTimeout=requestCacheTimeout;}

    public String getSysdateSql(){return sysdateSql;}
    public void setSysdateSql(String sysdateSql){this.sysdateSql = sysdateSql;}
    public int getPageSize(){return pageSize;}
    public void setPageSize(int pageSize){
        this.pageSize=pageSize;
    }
    public String getExclueModules(){return exclueModules;}
    public void setExclueModules(String exclueModules){this.exclueModules = exclueModules;}
    public String getExclueExecuters(){return exclueExecuters;}
    public void setExclueExecuters(String exclueExecuters){this.exclueExecuters = exclueExecuters;}

    public String getExclueLoginModules(){return exclueLoginModules;}
    public void setExclueLoginModules(String exclueLoginModules){this.exclueLoginModules = exclueLoginModules;}


    public String getExclueLoginExecuters(){return exclueLoginExecuters;}
    public void setExclueLoginExecuters(String exclueLoginExecuters){this.exclueLoginExecuters = exclueLoginExecuters;}


    public String getRunningMode(){return runningMode;}
    public void setRunningMode(String runningMode){this.runningMode = runningMode;}
}
