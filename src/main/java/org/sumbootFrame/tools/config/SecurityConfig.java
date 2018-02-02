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
    private Map<String, String> xssMap;
}
