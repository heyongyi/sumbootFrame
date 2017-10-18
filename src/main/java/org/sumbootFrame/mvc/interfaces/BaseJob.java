package org.sumbootFrame.mvc.interfaces;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by thinkpad on 2017/10/18.
 */
public interface BaseJob extends Job {
    public void execute(JobExecutionContext context) throws JobExecutionException;
}