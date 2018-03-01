package org.sumbootFrame;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.rpc.netty.server.DefaultServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.sumbootFrame.tools.config.KaptchaConfig;

import java.util.Properties;

/**
 * Created by thinkpad on 2018/1/24.
 */
@Configuration
public class BeansConfig {
    @Autowired
    private KaptchaConfig kaptchaConfig;
    @Bean(name="captchaProducer")
    public DefaultKaptcha getDefaultKaptcha(){
        com.google.code.kaptcha.impl.DefaultKaptcha defaultKaptcha = new com.google.code.kaptcha.impl.DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", kaptchaConfig.getBorder());
        properties.setProperty("kaptcha.border.color", kaptchaConfig.getBordercolor());
        properties.setProperty("kaptcha.textproducer.font.color", kaptchaConfig.getTextproducerfontcolor());
        properties.setProperty("kaptcha.image.width", kaptchaConfig.getImagewidth());
        properties.setProperty("kaptcha.image.height", kaptchaConfig.getImageheight());
        properties.setProperty("kaptcha.textproducer.font.size", kaptchaConfig.getFontsize());
        properties.setProperty("kaptcha.textproducer.char.length", kaptchaConfig.getTextproducercharlength());
        properties.setProperty("kaptcha.textproducer.font.names", kaptchaConfig.getTextproducerfontnames());
        properties.setProperty("kaptcha.textproducer.char.string",kaptchaConfig.getTextproducercharstring());
        properties.setProperty("kaptcha.background.clear.from",kaptchaConfig.getBackgroundclearfrom());
        properties.setProperty("kaptcha.background.clear.from",kaptchaConfig.getBackgroundclearto());
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }

    @Bean
    public DefaultServer defaultServer() {
        return new DefaultServer();
    }
}
