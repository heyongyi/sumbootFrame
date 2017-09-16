package org.sumboot.sumFrame.utils.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by thinkpad on 2017/9/16.
 */
@ConfigurationProperties(prefix = "sum.constants")
@PropertySource("classpath:constants.properties")
@Component
public class ConstantsConfig {
    private Map<String, String> aesPassKey;
    public Map<String,String> getAesPassKey(){return aesPassKey;}
    public void setAesPassKey(Map<String, String> aesPassKey){this.aesPassKey=aesPassKey;}
}
