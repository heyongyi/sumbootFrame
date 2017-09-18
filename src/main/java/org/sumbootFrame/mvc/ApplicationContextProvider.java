package org.sumbootFrame.mvc;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Created by thinkpad on 2017/9/12.
 */
@Service
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
