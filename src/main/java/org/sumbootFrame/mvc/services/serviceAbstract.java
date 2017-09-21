package org.sumbootFrame.mvc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.sumbootFrame.data.dao.common.MyBatisCommDAO;
import org.sumbootFrame.mvc.interfaces.IDao;
import org.sumbootFrame.mvc.interfaces.ServiceInterface;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.tools.config.AppConfig;
import org.sumbootFrame.tools.config.AuthorityConfig;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thinkpad on 2017/9/13.
 */
public abstract class serviceAbstract implements ServiceInterface {
    private HashMap<String,Object> outpool = new HashMap<String,Object>();//输出参数池
    private HashMap<String,Object> inpool = new HashMap<>();//输入参数池
    private int pageNum = 1;//分页
    private HashMap<String, Object> context;

    @Resource
    Map<String, IDao> daoFactory;
    @Autowired
    AppConfig appconf;
    @Autowired
    AuthorityConfig authconfig;
    /**********************服务层调用函数**********************************/
    public abstract ReturnUtil execute() throws Exception;

    public HashMap<String, Object> getSession() {
        HashMap<String, Object> session = (HashMap<String, Object>) this.getContext().get("session");
        return session;
    }
    public HashMap<String, Object> getCache(){
        HashMap<String, Object> session = (HashMap<String, Object>) this.getContext().get("cache");
        return session;
    }
    public String getAppName(){return appconf.getName();}
    public String getSysdateSql() {
        return appconf.getSysdateSql();
    }
    public Map<String, IDao> getDaoFactory() {return daoFactory;}
    public void setDaoFactory(Map<String, IDao> daoFactory) {
        this.daoFactory = daoFactory;
    }

    public String getSysdate() {
        MyBatisCommDAO myBatisCommDAO=(MyBatisCommDAO) this.getDaoFactory().get("myBatisCommDAO");
        return myBatisCommDAO.getSysdate(appconf.getSysdateSql());
    }

    public String getSequence(String sequenceName) {
        MyBatisCommDAO myBatisCommDAO=(MyBatisCommDAO) this.getDaoFactory().get("myBatisCommDAO");
        return myBatisCommDAO.getSequence(sequenceName);
    }

    public String getOraSequence(String sequenceName) {
        MyBatisCommDAO myBatisCommDAO=(MyBatisCommDAO) this.getDaoFactory().get("myBatisCommDAO");
        return myBatisCommDAO.getOraSequence(sequenceName);
    }
    public int getPageSize() {
        return appconf.getPageSize();
    }

    public void setPageSize(int pageSize) {
        appconf.setPageSize(pageSize);
    }

    public int getPageNum() {
        return pageNum;
    }
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getCaptchaKey(){return authconfig.getCaptchaKey();}

    /**********************控制层调用函数**********************************/
    @Override
    public HashMap<String, Object> getContext() {return context;}
    @Override
    public void setContext(HashMap<String, Object> context) {this.context = context;}
    @Override
    public HashMap<String, Object> getoutpool() throws Exception {return outpool;}
    @Override
    public void setinpool(HashMap inpoll) throws Exception{this.inpool = inpoll;}
    @Override
    public HashMap<String, Object> getinpool() throws Exception {return inpool;}
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=360,rollbackFor=RuntimeException.class)
    public ReturnUtil dealface() throws Exception {
        beforeQuery();
        return execute();
    }

    public void initParam() throws Exception {
        HashMap<String, Object> inpool =this.getinpool();
        if (inpool != null) {
            this.setPageNum(inpool.get("page") == null ? this.getPageNum() : Integer.valueOf(inpool.get(
                    "page").toString()));
            this.setPageSize(inpool.get("size") == null ? this.getPageSize() : Integer.valueOf(inpool.get(
                    "size").toString()));
        }
    }
    public void beforeQuery() throws Exception {
        initParam();
    }
}
