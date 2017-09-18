package org.sumboot.sumFrame.mvc.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.sumboot.sumFrame.data.dao.common.MyBatisDAO;
import org.sumboot.sumFrame.utils.RETURN;
import org.sumboot.sumFrame.tools.ReturnUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thinkpad on 2017/9/13.
 */
@Service
public class defaultService extends serviceAbstract{
    @Override
    public ReturnUtil execute() throws Exception {
        HashMap<String, Object> Param = this.getinpool();//form
        String dealType = (String)Param.get("deal-type");
        switch(dealType) {
            case "download":
                this.getoutpool().put("downLoadPath","D:\\idealspace\\sumbootFrame\\src\\main\\resources\\static\\apidemo.adoc");
                this.getoutpool().put("fileName","apidemo.adoc");
                break;
            case "getParam":
                this.getoutpool().put("remote-ip",this.getinpool().get("remote-ip"));
                this.getoutpool().put("url",this.getinpool().get("url"));
                this.getoutpool().put("referer",this.getinpool().get("referer"));
                break;
            case "PAGEtest":
                MyBatisDAO myBatisDAO=(MyBatisDAO) this.getDaoFactory().get("myBatisDAO");
                PageHelper.startPage(this.getPageNum(), this.getPageSize());//getValidStaffListBycond
                List<HashMap<String,String>> array = (List<HashMap<String,String>>) myBatisDAO.getCartAttribute();
                PageInfo page = new PageInfo(array);
                this.getoutpool().put("CartAttr",page);
                break;
            default:
                break;
        }
        return RETURN.SUCCESS;
    }
}
