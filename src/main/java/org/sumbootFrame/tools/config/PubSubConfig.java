package org.sumbootFrame.tools.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by thinkpad on 2017/9/12.
 */

@ConfigurationProperties(prefix = "sum.pubsub")
@PropertySource({
        "classpath:application.properties"
})
@Component
public class PubSubConfig {
    private String subscriberCenter ;
    private String subChannel;
    private String client;
    private String messagetxid;
    private String registerChannel;
    private int interval;
    public String getSubscriberCenter() {
        return subscriberCenter;
    }

    public void setSubscriberCenter(String subscriberCenter) {
        this.subscriberCenter = subscriberCenter;
    }

    public String getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(String subChannel) {
        this.subChannel = subChannel;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getMessagetxid() {
        return messagetxid;
    }

    public void setMessagetxid(String messagetxid) {
        this.messagetxid = messagetxid;
    }

    public String getRegisterChannel() {
        return registerChannel;
    }

    public void setRegisterChannel(String registerChannel) {
        this.registerChannel = registerChannel;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
