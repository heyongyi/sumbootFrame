package org.sumbootFrame.tools;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.sumbootFrame.data.dao.primary.defaulttest.PrimaryDAO;
import org.sumbootFrame.mvc.controller.ApplicationContextProvider;
import org.sumbootFrame.mvc.interfaces.BaseJob;
import org.sumbootFrame.mvc.interfaces.IDao;
import org.sumbootFrame.mvc.interfaces.ServiceInterface;
import org.sumbootFrame.mvc.services.defaulttest.defaultService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thinkpad on 2017/10/18.
 */
@Component
public class NewJob implements BaseJob {
    private static Logger _log = LoggerFactory.getLogger(NewJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        _log.error("New Job执行时间: " + new Date());
        ServiceInterface defaultService = (ServiceInterface) ApplicationContextProvider.getApplicationContext().getBean("defaultService");
        PrimaryDAO primaryDAO=(PrimaryDAO) defaultService.getDaoFactory().get("primaryDAO");
        List<HashMap<String,String>> array = (List<HashMap<String,String>>) primaryDAO.getCartAttribute();
        _log.error("New Job"+array.size());
    }
}
