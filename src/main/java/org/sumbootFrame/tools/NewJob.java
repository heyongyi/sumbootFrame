package org.sumbootFrame.tools;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sumbootFrame.mvc.interfaces.BaseJob;

import java.util.Date;

/**
 * Created by thinkpad on 2017/10/18.
 */
public class NewJob implements BaseJob {
    private static Logger _log = LoggerFactory.getLogger(NewJob.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        _log.error("New Job执行时间: " + new Date());
    }
}
