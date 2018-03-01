package org.sumbootFrame.tools.config;
import org.springframework.beans.factory.annotation.Value;
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

    private String moduleName ;
    private String tokenName;
    private String sessionChannel;
    private String cacheChanel;
    private long sessionTimeout;
    private long cacheTimeout;
    private int pageSize;
    private boolean exclueModules;
    private String exclueExecuters;
    private boolean exclueLoginModules;
    private String exclueLoginExecuters;
    private String runningMode;
    private String contextPath;
    private String errorView;

    public String getContextPath(){return contextPath ;}
    public void setContextPath(String contextPath){this.contextPath=contextPath;}


    public String getTokenName(){return moduleName+"Token";}
    public String getSessionChannel(){return moduleName+"Session";}
    public String getCacheChanel(){return moduleName+"Cache";}

    public long getSessionTimeout(){return sessionTimeout;}
    public void setSessionTimeout(long sessionTimeout){this.sessionTimeout = sessionTimeout;}
    public long getCacheTimeout(){return cacheTimeout;}
    public void setCacheTimeout(long requestCacheTimeout){this.cacheTimeout=requestCacheTimeout;}

    public int getPageSize(){return pageSize;}
    public void setPageSize(int pageSize){
        this.pageSize=pageSize;
    }

    public String getExclueExecuters(){return exclueExecuters;}
    public void setExclueExecuters(String exclueExecuters){this.exclueExecuters = exclueExecuters;}


    public String getExclueLoginExecuters(){return exclueLoginExecuters;}
    public void setExclueLoginExecuters(String exclueLoginExecuters){this.exclueLoginExecuters = exclueLoginExecuters;}


    public String getRunningMode(){return runningMode;}
    public void setRunningMode(String runningMode){this.runningMode = runningMode;}


    public String getErrorView() {
        return errorView;
    }

    public void setErrorView(String errorView) {
        this.errorView = errorView;
    }

    public boolean getExclueModules() {
        return exclueModules;
    }

    public void setExclueModules(boolean exclueModules) {
        this.exclueModules = exclueModules;
    }

    public boolean getExclueLoginModules() {
        return exclueLoginModules;
    }

    public void setExclueLoginModules(boolean exclueLoginModules) {
        this.exclueLoginModules = exclueLoginModules;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
