package org.sumbootFrame.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.sumbootFrame.tools.config.ResponceConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by thinkpad on 2017/9/18.
 */
@Controller
public class DownloadController {
    @Autowired
    ResponceConfig responceconf;
    private void handleResponseHeader(HttpServletResponse response, String referer) {
        if (referer != null) {
            String[] responseHeaderOrigin = responceconf.getHeader().getOrigin().split(",");
            for (String origin : responseHeaderOrigin) {
                //1、如果orgin配置为 http://*.hearglobal.com 校验逻辑如下
                //2、比对请求referer 的一级域名部分是否同orgin 的一级域名部分相同。
                //3、如果相同，则将orgin http://* 替换成referer的二级域名部分作为origin返回
                if (origin.startsWith("http://*.")||origin.startsWith("https://*.")) {
                    if (referer.substring(referer.indexOf(".") + 1).startsWith(origin.substring(origin.indexOf(".") + 1))) {
                        //由于origin不支持http://*.hearglobal.com配置，所以将http://* 替换成 referer的二级域名部分。
                        origin = origin.replace(origin.substring(0, origin.indexOf(".")), referer.substring(0, referer.indexOf(".")));
                        response.setHeader("Access-Control-Allow-Origin", origin);
                        response.setHeader("Access-Control-Allow-Methods", responceconf.getHeader().getMethods());
                        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(responceconf.getHeader().getCredentials()));
                        response.setHeader("Access-Control-Allow-Headers", responceconf.getHeader().getHeaders());
                        break;
                    }
                } else {
                    if (referer.startsWith(origin)) {
                        response.setHeader("Access-Control-Allow-Origin", origin);
                        response.setHeader("Access-Control-Allow-Methods", responceconf.getHeader().getMethods());
                        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(responceconf.getHeader().getCredentials()));
                        response.setHeader("Access-Control-Allow-Headers", responceconf.getHeader().getHeaders());
                        break;
                    }
                }
            }
        }
    }
    @RequestMapping(value = "/{module}/download",method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView downloadcore(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @PathVariable String module,
                                     @RequestParam(value = "dp", required = false) String downLoadPath,
                                     @RequestParam(value = "fn", required = false) String fileName)throws IOException {
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;
        try {
            long fileLength = new File(downLoadPath).length();
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-Length", String.valueOf(fileLength));
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "utf-8"));
            handleResponseHeader(response, request.getHeader("referer"));
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return null;
    }
}
