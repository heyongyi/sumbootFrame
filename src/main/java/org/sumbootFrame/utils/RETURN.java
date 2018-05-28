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
    public static ReturnUtil CHK_PASS_ERROR = new ReturnUtil("51002", "登录验证，密码不正确");
    public static ReturnUtil QUERY_NO_RESULT = new ReturnUtil("52001", "根据条件查询无结果");
    public static ReturnUtil CATCH_EXECUTE_FAILED = new ReturnUtil("53001", "系统抛出异常，请联系管理员查看");
    public static ReturnUtil REQUIRED_REQ_PARAM_IS_NULL = new ReturnUtil("01001", "必填的请求参数不能为空");
    public static ReturnUtil GOODS_TYPE_ID_IS_NOT_EXIST = new ReturnUtil("01002", "goods_type_id不存在");
    public static ReturnUtil NAME_IS_EXIST = new ReturnUtil("01003", "名称已存在，请更换为其他名称");
    public static ReturnUtil ERROR_DATA = new ReturnUtil("01004", "数据格式不正确或数据为空");
    public static ReturnUtil PRODUCT_ID_IS_NOT_EXIST = new ReturnUtil("01005", "product_id不存在");
    public static ReturnUtil PRODUCT_CANCEL_SALE_OR_DEL = new ReturnUtil("01006", "商品已下架或者删除");
    public static ReturnUtil SKU_CANCEL_SALE_OR_DEL = new ReturnUtil("01007", "sku已下架或者删除");
    public static ReturnUtil SKU_ID_IS_NOT_EXIST = new ReturnUtil("01007", "sku_id不存在");
    public static ReturnUtil PARAM_IS_NOT_READY = new ReturnUtil("01008", "参数不完整");
    public static ReturnUtil PSPT_IS_NOT_RIGHT = new ReturnUtil("01009", "身份证信息不符合");
    public static ReturnUtil NO_URL_SETTING = new ReturnUtil("50002", "没找到URL配置");
    public static ReturnUtil OUT_OF_TIMES = new ReturnUtil("50002", "超出最大允许次数");
    public static ReturnUtil INTERNET_ERROR = new ReturnUtil("52001", "调用接口出错!");
    public static ReturnUtil SES_TIME_OUT = new ReturnUtil("10005", "请先登录");
    public static ReturnUtil IDENTIFY_ERROR = new ReturnUtil("10006", "身份信息错误");
    public static ReturnUtil LOSE_PARAM = new ReturnUtil("10007", "缺少参数");
    public static ReturnUtil EXTDEAL_NO_SUCCESS  = new ReturnUtil("10517", "调用外部接口，操作失败");
}
