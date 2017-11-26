package org.sumbootFrame.tools.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by thinkpad on 2017/9/12.
 */

@ConfigurationProperties(prefix = "spring.redis")
@PropertySource({
        "classpath:application.properties"
})
@Component
public class RedisConfig {
    private String password;

    private RedisConfig.Cluster cluster;
    public static class Cluster {
        private List<String> nodes;
        private Integer maxRedirects;

        public Cluster() {
        }

        public List<String> getNodes() {
            return this.nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public Integer getMaxRedirects() {
            return this.maxRedirects;
        }

        public void setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
        }
    }
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public RedisConfig.Cluster getCluster() {
        return this.cluster;
    }

    public void setCluster(RedisConfig.Cluster cluster) {
        this.cluster = cluster;
    }

}
