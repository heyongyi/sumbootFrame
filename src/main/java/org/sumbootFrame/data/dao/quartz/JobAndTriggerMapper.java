package org.sumbootFrame.data.dao.quartz;

import org.springframework.stereotype.Component;
import org.sumbootFrame.mvc.interfaces.IDao;
import org.sumbootFrame.tools.JobAndTrigger;

import java.util.List;

/**
 * Created by thinkpad on 2017/10/18.
 */
@Component
public interface JobAndTriggerMapper {
    public List<JobAndTrigger> getJobAndTriggerDetails();
}
