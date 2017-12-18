package org.sumbootFrame.mvc.services;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.sumbootFrame.data.dao.primary.common.PrimaryCommDAO;
import org.sumbootFrame.mvc.interfaces.IDao;
import org.sumbootFrame.mvc.interfaces.ServiceInterface;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.tools.config.AppConfig;
import org.sumbootFrame.tools.config.AuthorityConfig;
import org.sumbootFrame.tools.exception.MyException;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thinkpad on 2017/9/13.
 */
public abstract class serviceAbstract implements ServiceInterface {
    protected Logger logger = Logger.getLogger(this.getClass());
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
    public abstract ReturnUtil query() throws Exception;
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
    @Override
    public Map<String, IDao> getDaoFactory() {return daoFactory;}
    public void setDaoFactory(Map<String, IDao> daoFactory) {
        this.daoFactory = daoFactory;
    }

    public String getSysdate() {
        PrimaryCommDAO primaryCommDAO =(PrimaryCommDAO) this.getDaoFactory().get("primaryCommDAO");
        return primaryCommDAO.getSysdate(appconf.getSysdateSql());
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
    public String getLoginPage(){return authconfig.getLoginPage();}
    public String getSessionObjName(){return authconfig.getSessionObjName();}
    public HashMap<String,Object> getSessionObj(){return ((HashMap<String,Object>)this.getSession().get(getSessionObjName()));}

    /**********************控制层调用函数**********************************/
    @Override
    public HashMap<String, Object> getContext() {return context;}
    @Override
    public void setContext(HashMap<String, Object> context) {this.context = context;}
    @Override
    public HashMap<String, Object> getoutpool() {return outpool;}
    @Override
    public void setinpool(HashMap inpoll){this.inpool = inpoll;}
    @Override
    public HashMap<String, Object> getinpool() {return inpool;}

    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    public ReturnUtil queryface() throws Exception {
        try {
            serviceLog("begin");
            beforeQuery();
            ReturnUtil ret = query();
            if (ret.getStateCode() != ReturnUtil.SUCCESS.getStateCode()) {
                throw new MyException(ret);
            }else{
                serviceLog("end");
            }
            return ret;
        }catch (Exception e){
            if( Integer.parseInt(appconf.getRunningMode())<2) {
                this.getoutpool().put("errorDetail", e);
            }else{
                this.getoutpool().put("errorDetail", e.getMessage());
            }
            if( Integer.parseInt(appconf.getRunningMode())<3) {
                logger.debug("SUM boot=>", e);
            }
            serviceLog("end");
            throw new MyException(ReturnUtil.THROW_ERROR);
        }
    }
    @Transactional(value = "primaryTransactionManager",propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=360,rollbackFor=RuntimeException.class)
    public ReturnUtil dealface() throws Exception {
        try {
            serviceLog("begin");
            ReturnUtil ret = execute();
            if (ret.getStateCode() != ReturnUtil.SUCCESS.getStateCode()) {
                throw new MyException(ret);
            }else{
                serviceLog("end");
            }
            return ret;
        }catch (Exception e){
            if( Integer.parseInt(appconf.getRunningMode())<2) {
                this.getoutpool().put("errorDetail", e);
            }else{
                this.getoutpool().put("errorDetail", e.getMessage());
            }
            if( Integer.parseInt(appconf.getRunningMode())<3) {
                logger.debug("SUM boot=>", e);
            }
            serviceLog("end");
            throw new MyException(ReturnUtil.THROW_ERROR);
        }
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
    public HashMap<String, Object> getCookies() {
        HashMap<String, Object> cookies = (HashMap<String, Object>) this.getContext().get("cookies");
        return cookies;
    }
    public String getOperatorStr() {
        HashMap<String, Object> pageUri = (HashMap<String, Object>) this.getinpool();
        String operatorStr = pageUri.get("executor") + "=>" + pageUri.get("deal-type");
        return operatorStr;
    }
    private void serviceLog(String type) {
        if(type.equals("begin")){
            logger.info("########################[BEGIN:" + this.getOperatorStr() + "]##########################");
            logger.debug("Cookies:" + this.getCookies());
            logger.debug("Session:" + this.getSession());
            logger.debug("InParam:" + this.getinpool());
            logger.debug("+--------------------------------------------------------------------------+");
        }
        else if(type.equals("end")){
            logger.debug("+--------------------------------------------------------------------------+");
            logger.debug("Cookies:" + this.getCookies());
            logger.debug("Session:" + this.getSession());
            logger.debug("OutParam:" + this.getoutpool());
            logger.info("########################[END  :" + this.getOperatorStr() + "]##########################");
        }
        else{
            //to do
        }
    }
}
