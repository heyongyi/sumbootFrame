package org.sumbootFrame;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by thinkpad on 2017/9/17.
 */
@Configuration
public class sumbootConfigurer extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AuthcenterInterceptor()).addPathPatterns("/**");
//        super.addInterceptors(registry);
    }
}
