package org.sumbootFrame.mvc.interfaces;



import org.sumbootFrame.tools.ReturnUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thinkpad on 2016/12/12.
 */
public interface ServiceInterface {
    /*控制层暴露接口*/
    public ReturnUtil initface() throws Exception;
    public ReturnUtil dealface() throws Exception;
    public ReturnUtil queryface() throws Exception;
    public HashMap<String,Object> getoutpool();   //对上层提供获取输出，和写入输入就够了
    /****************************inpool包括 form jsonbody urlparam***********************/
    public void setinpool(HashMap inpoll);
    public HashMap<String,Object> getinpool();
    /****************************context包括 cache session***********************/
    public void setContext(HashMap<String, Object> context);
    public HashMap<String, Object> getContext();

    public Map<String, IDao> getDaoFactory() ;
    public String getLogicView() ;
}
