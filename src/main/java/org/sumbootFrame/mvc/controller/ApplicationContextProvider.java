package org.sumbootFrame.mvc.controller;

/**
 * Created by thinkpad on 2016/12/12.
 */
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class ApplicationContextProvider implements ApplicationListener<ContextRefreshedEvent> {
    private static ApplicationContext applicationContext = null;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(applicationContext == null){
            applicationContext = event.getApplicationContext();
        }
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}