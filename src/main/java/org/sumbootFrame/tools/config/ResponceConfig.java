package org.sumbootFrame.tools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Created by thinkpad on 2017/9/12.
 */
@ConfigurationProperties(prefix = "sum.response")
@PropertySource({
        "classpath:response.properties",
        "classpath:static/property/response-self.properties"
})
@Component
public class ResponceConfig {
    @NotNull
    private Header header ;
    public static class Header {
        private String origin;
        private String methods;
        private boolean credentials;
        private String headers;

        public String getOrigin(){return origin;}
        public void setOrigin(String origin){this.origin = origin;}
        public String getMethods(){return methods;}
        public boolean getCredentials(){return credentials;}
        public String getHeaders(){return headers;}
    }
    public Header getHeader() {return this.header;}
    public void setHeader(Header header){this.header = header;}
}
