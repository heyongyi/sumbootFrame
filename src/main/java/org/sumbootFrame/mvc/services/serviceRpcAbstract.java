package org.sumbootFrame.mvc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.sumbootFrame.mvc.interfaces.IDao;
import org.sumbootFrame.mvc.interfaces.ServiceRpcInterface;
import org.sumbootFrame.tools.DBTools;
import org.sumbootFrame.tools.JedisClusterUtil;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.tools.exception.MyException;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by thinkpad on 2018/2/26.
 */
public abstract class serviceRpcAbstract implements ServiceRpcInterface {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(serviceRpcAbstract.class);
    private HashMap<String,Object> outpool = new HashMap<String,Object>();//输出参数池
    private HashMap<String,Object> inpool = new HashMap<String,Object>();//输入参数池
    private int pageNum = 1;//分页
    private HashMap<String, Object> context;
    protected static final HashMap<String,Object> appconf = makePropertyConfig("sum.app");

    private SqlSession daoFactory = DBTools.getSession();
    /*******************************服务层调用函数**********************************/
    public abstract ReturnUtil init() throws Exception;
    public abstract ReturnUtil query() throws Exception;
    public abstract ReturnUtil execute() throws Exception;

    public HashMap<String, Object> getCookies() {
        HashMap<String, Object> cookies = (HashMap<String, Object>) this.getContext().get("cookies");
        return cookies;
    }
    public int getPageSize() {
        return Integer.parseInt(appconf.get("pageSize").toString());
    }
    public void setPageSize(int pageSize) {
        appconf.put("pageSize",pageSize);
    }
    public int getPageNum() {
        return pageNum;
    }
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    private static HashMap<String,Object> makePropertyConfig(String prefix){
        HashMap<String,Object> sumMap = new HashMap<>();
        InputStream defin = serviceRpcAbstract.class.getClassLoader().getResourceAsStream( "properties/sum.properties" );
        InputStream prein = serviceRpcAbstract.class.getClassLoader().getResourceAsStream( "self-properties/sum-self.properties" );
        try {
            Properties prop =  new  Properties();
            prop.load(defin);
            Set<Object> sumProKeySet=prop.keySet();
            for(Object fullkey:sumProKeySet){
                String skey = (String)fullkey;
                if(skey.startsWith(prefix+".")){
                    String key = skey.split(prefix+".")[1];
                    sumMap.put(key,prop.getProperty(skey).trim());
                }
            }
            defin.close();


            prop =  new  Properties();
            prop.load(prein);
            sumProKeySet=prop.keySet();
            for(Object fullkey:sumProKeySet){
                String skey = (String)fullkey;
                if(skey.startsWith(prefix+".")){
                    String key = skey.split(prefix+".")[1];
                    sumMap.put(key,prop.getProperty(skey).trim());
                }
            }
            prein.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sumMap;
    }

    @Override
    public ReturnUtil initface() throws Exception {
        try {
            serviceLog("begin");
            ReturnUtil ret = init();
            if (ret.getStateCode() != ReturnUtil.SUCCESS.getStateCode()) {
                this.getDaoFactory().rollback();
                throw new MyException(ret);
            }else{
                this.getDaoFactory().commit();
                serviceLog("end");
            }
            return ret;
        }catch (Exception e){
            return dealCatch(e);
        }
    }

    @Override
    public ReturnUtil dealface() throws Exception {
        try {
            serviceLog("begin");
            ReturnUtil ret = execute();
            if (ret.getStateCode() != ReturnUtil.SUCCESS.getStateCode()) {
                this.getDaoFactory().rollback();
                throw new MyException(ret);
            }else{
                this.getDaoFactory().commit();
                serviceLog("end");
            }
            return ret;
        }catch (Exception e){
            return dealCatch(e);
        }
    }

    @Override
    public ReturnUtil queryface() throws Exception {
        try {
            serviceLog("begin");
            initParam();
            ReturnUtil ret = query();
            if (ret.getStateCode() != ReturnUtil.SUCCESS.getStateCode()) {
                throw new MyException(ret);
            }else{
                serviceLog("end");
            }
            return ret;
        }catch (Exception e){
            return dealCatch(e);
        }
    }

    @Override
    public HashMap<String, Object> getoutpool() {
        return outpool;
    }
    @Override
    public void setinpool(HashMap inpool) {
        this.inpool = inpool;
    }
    @Override
    public HashMap<String, Object> getinpool() {
        return inpool;
    }

    @Override
    public void setContext(HashMap<String, Object> context) {
        this.context = context;
    }

    @Override
    public HashMap<String, Object> getContext() {
        return context;
    }
    private ReturnUtil dealCatch(Exception e) throws Exception{
        //异常情况清除outpool
        this.getoutpool().clear();
        if( Integer.parseInt(appconf.get("runningMode").toString())<2) {//01
            this.getoutpool().put("errorDetail", e);
        }else{
            this.getoutpool().put("errorDetail", e.getMessage());
        }
        if( Integer.parseInt(appconf.get("runningMode").toString())<3) {//012
            logger.debug("SUM boot=>", e);
        }
        serviceLog("end");
        if(e.getClass().equals(MyException.class)){
            throw e;
        }else{
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

    public String getOperatorStr() {
        HashMap<String, Object> pageUri = (HashMap<String, Object>) this.getinpool();
        String operatorStr = pageUri.get("executor") + "=>" + pageUri.get("deal-type");
        return operatorStr;
    }
    private void serviceLog(String type) {
        if(type.equals("begin")){
            logger.info("########################[BEGIN:" + this.getOperatorStr() + "]##########################");
            logger.debug("Cookies:" + this.getCookies());
            logger.debug("InParam:" + this.getinpool());
            logger.debug("+--------------------------------------------------------------------------+");
        }
        else if(type.equals("end")){
            logger.debug("+--------------------------------------------------------------------------+");
            logger.debug("Cookies:" + this.getCookies());
            logger.debug("OutParam:" + this.getoutpool());
            logger.info("########################[END  :" + this.getOperatorStr() + "]##########################");
        }
        else{
            //to do
        }
    }
    public SqlSession getDaoFactory() {
        return daoFactory;
    }
}
