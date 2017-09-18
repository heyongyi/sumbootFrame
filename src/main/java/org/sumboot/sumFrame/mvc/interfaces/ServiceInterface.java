package org.sumboot.sumFrame.mvc.interfaces;



import org.sumboot.sumFrame.tools.ReturnUtil;

import java.util.HashMap;

/**
 * Created by thinkpad on 2016/12/12.
 */
public interface ServiceInterface {
    /*控制层暴露接口*/
    public ReturnUtil dealface() throws Exception;

    public HashMap<String,Object> getoutpool() throws Exception;   //对上层提供获取输出，和写入输入就够了
    /****************************inpool包括 form jsonbody urlparam***********************/
    public void setinpool(HashMap inpoll) throws Exception;
    public HashMap<String,Object> getinpool()throws Exception;
    /****************************context包括 cache session***********************/
    public void setContext(HashMap<String, Object> context);
    public HashMap<String, Object> getContext();

}
