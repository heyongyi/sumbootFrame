package org.sumbootFrame.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.sumbootFrame.DemoApplicationTests;
import org.sumbootFrame.data.mao.RedisDao;
import org.sumbootFrame.mvc.controller.MainController;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
/**
 * Created by thinkpad on 2017/9/14.
 */

public class defaultServiceTest extends DemoApplicationTests {

    //?mt=1&deal-type=PAGEtest&page=1300&size=20
    @Test
    public void testPAGEtest() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("deal-type", "PAGEtest");
        params.add("page", "1300");
        params.add("size", "20");
        this.mockMvc.perform(
                get("/{module}/{excuter}","sum-mod","default-service").params(params)
                        .accept(MediaType.APPLICATION_JSON)
        .cookie(new Cookie("sumbootToken","12345678123456781234567812345678")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"stateCode\" : \"00000\"")))
                .andDo(document("default-service",
                        requestParameters(
                                parameterWithName("deal-type").description("执行逻辑"),
                                parameterWithName("page").description("分页页码"),
                                parameterWithName("size").description("分页大小")),
                        relaxedResponseFields(
                                fieldWithPath("header.appName").type("String").description("组件名"),
                                fieldWithPath("header.stateMsg").type("String").description("返回信息"),
                                fieldWithPath("header.stateCode").type("String").description("返回状态编码"),
                                fieldWithPath("header.success").type("String").description("是否成功"),
                                fieldWithPath("dataSet.CartAttr.pageSize").type("String").description("分页大小"),
                                fieldWithPath("dataSet.CartAttr.pageNum").type("String").description("分页大小"),
                                fieldWithPath("dataSet.CartAttr.total").type("String").description("查询数据总数"),
                                fieldWithPath("dataSet.CartAttr.pages").type("int").description("查询数据总页数"),
                                fieldWithPath("dataSet.CartAttr.startRow").type("int").description("当前页起始行数"),
                                fieldWithPath("dataSet.CartAttr.endRow").type("int").description("当前页结束行数"),
                                fieldWithPath("dataSet.CartAttr.list").type("对象数组").description("页结果集"))));
    }
}
