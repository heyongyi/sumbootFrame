package org.sumbootFrame;

import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * Created by thinkpad on 2017/9/17.
 */
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",

        initParams={

                @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")//忽略资源

        }

)
public class DruidStatFilter  extends WebStatFilter {
}
