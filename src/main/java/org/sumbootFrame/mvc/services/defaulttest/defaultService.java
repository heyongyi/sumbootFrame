package org.sumbootFrame.mvc.services.defaulttest;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.sumbootFrame.data.dao.primary.defaulttest.PrimaryDAO;
import org.sumbootFrame.data.dao.secondary.defaulttest.SecondaryDAO;
import org.sumbootFrame.mvc.services.serviceAbstract;
import org.sumbootFrame.utils.RETURN;
import org.sumbootFrame.tools.ReturnUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thinkpad on 2017/9/13.
 */
@Service
public class defaultService extends serviceAbstract {
    @Override
    public ReturnUtil execute() throws Exception {
        HashMap<String, Object> Param = this.getinpool();//form
        String dealType = (String)Param.get("deal-type");
        PageInfo page;
        PageHelper.startPage(this.getPageNum(), this.getPageSize());//getValidStaffListBycond
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
                PrimaryDAO primaryDAO=(PrimaryDAO) this.getDaoFactory().get("primaryDAO");
                List<HashMap<String,String>> array = (List<HashMap<String,String>>) primaryDAO.getCartAttribute();
                page = new PageInfo(array);
                this.getoutpool().put("CartAttr",page);
                break;
            case "ddatasource":
                SecondaryDAO secondaryDAO = (SecondaryDAO)this.getDaoFactory().get("secondaryDAO");
                List<HashMap<String,String>> list = (List<HashMap<String,String>>) secondaryDAO.getEcsAreainfo();
                page = new PageInfo(list);
                this.getoutpool().put("EcsAreainfo",page);
                break;
            default:
                break;
        }
        return RETURN.SUCCESS;
    }
}
