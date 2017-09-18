package org.sumbootFrame.utils;

import org.sumbootFrame.tools.ReturnUtil;

/**
 * Created by thinkpad on 2017/7/24.
 */
public class RETURN {
    public static ReturnUtil SUCCESS = new ReturnUtil("00000", "服务执行成功！");
    public static ReturnUtil REDIRECT_TOKEN_ERROR = new ReturnUtil("30001", "重定向令牌发生变化！");
    public static ReturnUtil REQUEST_PARAM_NULL_ERROR = new ReturnUtil("50002", "request参数无效");
    public static ReturnUtil CHK_ERROR = new ReturnUtil("51001", "登录验证，验证码不正确");
    public static ReturnUtil QUERY_NO_RESULT = new ReturnUtil("52001", "根据条件查询无结果");
    public static ReturnUtil CATCH_EXECUTE_FAILED = new ReturnUtil("53001", "系统抛出异常，请联系管理员查看");

}
