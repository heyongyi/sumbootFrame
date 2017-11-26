package org.sumbootFrame.tools;

/**
 * Created by thinkpad on 2017/9/13.
 */
public final class ReturnUtil {

    private String stateCode = "";
    private String stateMsg = "";
    private String stateDetail = "";

    public String getStateCode() {return stateCode;}
    public ReturnUtil setStateCode(String stateCode) {this.stateCode=stateCode;return this;}
    public String getStateMsg() {
        return stateMsg;
    }
    public ReturnUtil setStateMsg(String stateMsg) {this.stateMsg=stateMsg;return this;}
    public String getStateDetail() {
        return stateDetail;
    }
    public ReturnUtil setStateDetail(String stateDetail) {this.stateDetail=stateDetail;return this;}

    public ReturnUtil(String stateCode, String stateMsg ) {
        super();
        this.stateCode = stateCode;
        this.stateMsg = stateMsg;
    }
    public ReturnUtil(String stateCode, String stateMsg ,String stateDetail ) {
        super();
        this.stateCode = stateCode;
        this.stateMsg = stateMsg;
        this.stateDetail = stateDetail;
    }

    public static ReturnUtil SUCCESS = new ReturnUtil("00000", "服务执行成功！");
    public static ReturnUtil REQUEST_PARAM_FORMAT_ERROR = new ReturnUtil("00001", "request参数格式化失败");
    public static ReturnUtil REQUEST_METHOD_NO_AUTH = new ReturnUtil("00002", "request参数格式化失败");
    public static ReturnUtil METHOD_ERROR = new ReturnUtil("00888", "执行逻辑不存在,请合适请求参数");
    public static ReturnUtil THROW_ERROR = new ReturnUtil("00999", "服务程序执行异常,请联系管理员");
}