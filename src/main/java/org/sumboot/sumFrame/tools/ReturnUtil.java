package org.sumboot.sumFrame.tools;

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


}