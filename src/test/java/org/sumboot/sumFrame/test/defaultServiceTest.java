package org.sumboot.sumFrame.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.sumboot.sumFrame.DemoApplicationTests;
import org.sumboot.sumFrame.mvc.controller.MainController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
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
    public void testQuery() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("mt", "1");
        params.add("deal-type", "PAGEtest");
        params.add("page", "1300");
        params.add("size", "20");
        this.mockMvc.perform(
                get("/{module}/{excuter}","sum-mod","default-service").params(params).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"stateCode\" : \"00000\"")))
                .andDo(document("default-service",
                        requestParameters(
                                parameterWithName("deal-type").description("执行逻辑"),
                                parameterWithName("page").description("分页页码"),
                                parameterWithName("size").description("分页大小"),
                                parameterWithName("mt").description("0:init 1:query 2:execute")
                        ),
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
