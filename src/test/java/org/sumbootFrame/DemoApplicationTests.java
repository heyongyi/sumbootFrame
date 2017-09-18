package org.sumbootFrame;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.sumbootFrame.tools.FormatJsonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	protected MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;
//	@Test
//	public void adocBuild() throws IOException {
//		String appDir = System.getProperty("user.dir");
//		String adocPath = appDir + "/src/main/asciidoc/api.adoc";
//		StringBuilder content = new StringBuilder();
////		content.append("include::" + appDir + "/src/main/asciidocs/preview.adoc");
//
//		File apidirs = new File(appDir + "/target/generated-snippets");
//		for (File apidir : apidirs.listFiles()) {
//			String apiName = apidir.getName();
//			content.append("=== " + apiName + " +\n\n");
//			fileAppend(content, apidir + "/curl-request.adoc", "Curl 请求");
//			fileAppend(content, apidir + "/httpie-request.adoc", "HTTPie 请求");
//			fileAppend(content, apidir + "/request-headers.adoc", "request-headers 类型说明");
//			fileAppend(content, apidir + "/http-request.adoc", "http-request");
//			fileAppend(content, apidir + "/request-parameters.adoc", "request-parameters类型说明");
//			fileAppend(content, apidir + "/request-body.adoc", "request-body类型说明");
//			fileAppend(content, apidir + "/http-response.adoc", "http-response");
//			fileAppend(content, apidir + "/response-fields.adoc", "response-fields 类型说明");
//		}
//		File file = new File(adocPath);
//		FileUtils.writeStringToFile(file, content.toString(), "utf-8");
//	}

	@Test
	public void adocBuild() throws IOException {
		String appDir = System.getProperty("user.dir");
		String adocPath = appDir + "/src/main/asciidoc/apidemo.adoc";
		StringBuilder content = new StringBuilder();
		File apidirs = new File(appDir + "/target/generated-snippets");
		content.append("= Spring REST Docs API 文档 Demo\n" +
				"作者 <heyy>\n" +
				"v1.0, 2017-06-15\n" +
				":toc: left\n" +
				":toclevels: 1\n" +
				":toc-title: 目  录\n" +
				":doctype: book\n" +
				":icons: font\n" +
				":operation-curl-request-title: Curl 请求\n" +
				":operation-httpie-request-title: HTTPie 请求\n" +
				":operation-http-request-title: Http 请求\n" +
				":operation-request-parameters-title: 请求参数说明\n" +
				":operation-request-fields-title: 请求参数说明\n" +
				":operation-http-response-title: Http 响应\n" +
				":operation-response-fields-title: Http 响应字段说明\n" +
				":operation-links-title: 相关链接\n\n");
		content.append("[[overview]]\n" +
				"== 概要\n" +
				"本文为API说明文档。\n" +
				"\n" +
				"[[overview-response]]\n" +
				"== 通用说明\n" +
				"\n" +
				"*RESTFul API 通用响应消息说明*\n" +
				"\n" +
				"|===\n" +
				"| 字段 | 类型 | 解释\n" +
				"\n" +
				"| header\n" +
				"| Object\n" +
				"| 响应头信息\n" +
				"\n" +
				"| dataSet\n" +
				"| Object\n" +
				"| 响应的数据\n" +
				"|===\n\n");
		for (File apidir : apidirs.listFiles()) {
			String apiName = apidir.getName();
			content.append("[[resources-"+apiName+"]]\n");
			content.append("== 1、" + apiName + "\n\n");
			content.append("operation::"+apiName+"[snippets='");
			content.append("curl-request,httpie-request,request-headers,http-request,request-parameters" +
					",http-response,response-fields']\n\n");
		}
		File file = new File(adocPath);
		FileUtils.writeStringToFile(file, content.toString(), "utf-8");
	}
	private void fileAppend(StringBuilder content, String s, String s1) {
		String value = readString1(s);
		if(value.length()<1){
			return;
		}
		if(s.contains("http-response")){
			content.append(s1+"\n"+ FormatJsonUtil.format(readString1(s))+"\n");
		}else{
			content.append(s1+"\n"+readString1(s)+"\n");
		}

	}

	@Before
	public void setUp() {
		//默认生成的文档片段
		Snippet[] defaultSnippets = new Snippet[]{CliDocumentation.curlRequest(), CliDocumentation.httpieRequest(), HttpDocumentation.httpRequest(), HttpDocumentation.httpResponse(), PayloadDocumentation.requestBody(), PayloadDocumentation.responseBody()};
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)
						//此处也支持生成markdown文档片段，但不能生成html
						.snippets().withTemplateFormat(TemplateFormats.asciidoctor()).withEncoding("UTF-8")
						.withDefaults(defaultSnippets)
						.and()
						.uris().withScheme("http").withHost("localhost").withPort(60804)
						.and()
				)
				.alwaysDo(print())
				.build();
	}
	public String readString1( String fileName)
	{
		File file = new File(fileName);
		if(!file.exists()){
			return "";
		}
		try
		{
			FileInputStream inStream= null;
			try {
				inStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int length=-1;
			while( (length = inStream.read(buffer)) != -1)
			{
				bos.write(buffer,0,length);
			}
			bos.close();
			inStream.close();
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return e.toString();
		}
	}

}
