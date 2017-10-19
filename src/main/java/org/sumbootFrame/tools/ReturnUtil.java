package org.sumbootFrame.tools;

/**
 * Created by thinkpad on 2017/9/13.
 */
public final class ReturnUtil {

    private String stateCode = "";
    private String stateMsg = "";

    public String getStateCode() {return stateCode;}
    public void setStateCode(String stateCode) {this.stateCode=stateCode;}
    public String getStateMsg() {
        return stateMsg;
    }
    public void setStateMsg(String stateMsg) {this.stateMsg=stateMsg;}

    public ReturnUtil(String stateCode, String stateMsg) {
        super();
        this.stateCode = stateCode;
        this.stateMsg = stateMsg;
    }
    public static ReturnUtil METHOD_ERROR = new ReturnUtil("88888", "执行逻辑不存在,请合适请求参数");
    public static ReturnUtil REQUEST_PARAM_FORMAT_ERROR = new ReturnUtil("50001", "request参数格式化失败");
    public static ReturnUtil SUCCESS = new ReturnUtil("00000", "服务执行成功！");

}