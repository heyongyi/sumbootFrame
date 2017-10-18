package org.sumbootFrame.mvc.interfaces;

import com.github.pagehelper.PageInfo;
import org.sumbootFrame.tools.JobAndTrigger;

/**
 * Created by thinkpad on 2017/10/18.
 */
public interface IJobAndTriggerService {
    public PageInfo<JobAndTrigger> getJobAndTriggerDetails(int pageNum, int pageSize);
}
