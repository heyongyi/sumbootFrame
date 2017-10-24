package org.sumbootFrame.tools.exception;

import org.sumbootFrame.tools.ReturnUtil;

/**
 * Created by thinkpad on 2017/10/19.
 */
public class MyException extends RuntimeException{
    private static final long serialVersionUID = 1646037390184618524L;
    private ReturnUtil ret;

    public MyException(ReturnUtil ret) {
        super(ret.getStateCode()+":"+(ret.getStateDetail().length()>0?ret.getStateDetail():ret.getStateMsg()));
        this.ret = ret;
    }

    public ReturnUtil getRet() {
        return ret;
    }

    public void setRet(ReturnUtil ret) {
        this.ret = ret;
    }
}
