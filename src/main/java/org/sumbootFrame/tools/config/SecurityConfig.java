package org.sumbootFrame.tools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by thinkpad on 2018/2/2.
 */
@ConfigurationProperties(prefix = "sum.security")
@PropertySource({
        "classpath:properties/security.properties",
        "classpath:self-properties/security-self.properties"
})
@Component
public class SecurityConfig {
    private Map<String, String> xssShieldMap;
    private Map<String, String> xssShiftMap;

    public Map<String, String> getXssShieldMap() {
        return xssShieldMap;
    }

    public void setXssShieldMap(Map<String, String> xssShieldMap) {
        this.xssShieldMap = xssShieldMap;
    }

    public Map<String, String> getXssShiftMap() {
        return xssShiftMap;
    }

    public void setXssShiftMap(Map<String, String> xssShiftMap) {
        this.xssShiftMap = xssShiftMap;
    }
}
