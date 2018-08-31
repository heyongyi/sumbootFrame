package org.sumbootFrame.mvc.services.web;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.sumbootFrame.data.dao.primary.PrimaryCommDAO;
import org.sumbootFrame.data.dao.secondary.SecondaryCommDAO;
import org.sumbootFrame.data.orm.User;
import org.sumbootFrame.mvc.services.serviceAbstract;
import org.sumbootFrame.tools.JedisClusterUtil;
import org.sumbootFrame.tools.ReturnUtil;
import org.sumbootFrame.tools.config.RedisConfig;
import org.sumbootFrame.utils.RETURN;
import redis.clients.jedis.JedisCluster;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by heyongyi on 2018/8/28.
 */
@Service
@Scope("request")
public class mongocliService extends serviceAbstract {
    @Autowired
    RedisConfig redisConfig;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Override
    public ReturnUtil init() throws Exception {
        return null;
    }

    @Override
    public ReturnUtil query() throws Exception {

        HashMap<String,Object> inpool = this.getinpool();
        String dealType = (String) inpool.get("deal-type");
        switch (dealType) {

        }






        return RETURN.SUCCESS;
    }

    @Override
    public ReturnUtil execute() throws Exception {
//        List<String> clusterNodes = redisConfig.getCluster().getNodes();
//        JedisCluster jedis = new JedisClusterUtil().cluster(redisConfig.getPassword(),(String[])clusterNodes.subList(0,redisConfig.getCluster().getMaxRedirects()).toArray(new String[redisConfig.getCluster().getMaxRedirects()]));
//        User user = new User(jedis.incr("mongo_id").intValue(),"Tseng", 22);
//        mongoTemplate.save(user);


        HashMap<String,Object> inpool = this.getinpool();
        String dealType = (String) inpool.get("deal-type");
        switch (dealType) {
            case "deleteFile":{
                String filename = (String)inpool.get("filename");
                gridFsTemplate.delete(new Query().addCriteria(Criteria.where("filename").is(filename)));
            }
            break;
            case "insertFile":
                DBObject metaData = new BasicDBObject();
                metaData.put("createdDate", new Date());
                metaData.put("realName","MongDB搭建分片.docx");
                String fileName = UUID.randomUUID().toString();
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream("E:\\SVNspace\\水瓶交接\\MongDB搭建分片.docx");
                    gridFsTemplate.store(inputStream, fileName, "image", metaData);
                } catch (IOException e) {
                    throw new RuntimeException("System Exception while handling request");
                }
            break;
            case "exportbyname":{
                String filename = (String)inpool.get("filename");
                List<GridFSDBFile> result = gridFsTemplate.find(new Query().addCriteria(Criteria.where("filename").is(filename)));


                if (result == null || result.size() == 0) {
                }else{
                    InputStream is = result.get(0).getInputStream();

                    BufferedInputStream in=null;
                    BufferedOutputStream out=null;
                    in=new BufferedInputStream(is);
                    out=new BufferedOutputStream(new FileOutputStream("E:\\"+result.get(0).getMetaData().get("realName")));
                    int len=-1;
                    byte[] b=new byte[102400];
                    while((len=in.read(b))!=-1){
                        out.write(b,0,len);
                    }
                    in.close();
                    out.close();

                }
            }

                break;
            case "exportbyid":{//new Query().addCriteria(Criteria.where("_id").is(new ObjectId(id)))
                String id = (String)inpool.get("id");
                GridFSDBFile result = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is(new ObjectId(id))));
                if (result == null ) {
                }else{
//                    IOUtils.toByteArray();
                    InputStream is = result.getInputStream();

                    BufferedInputStream in=null;
                    BufferedOutputStream out=null;
                    in=new BufferedInputStream(is);
                    out=new BufferedOutputStream(new FileOutputStream("E:\\"+result.getMetaData().get("realName")));
                    int len=-1;
                    byte[] b=new byte[102400];
                    while((len=in.read(b))!=-1){
                        out.write(b,0,len);
                    }
                    in.close();
                    out.close();

                }
            }
            case "findall":{
                GridFsResource[] txtFiles = gridFsTemplate.getResources("*");
                for (GridFsResource txtFile : txtFiles) {
                    System.out.println(txtFile.getFilename());
                }
            }

                break;
            case "findlike":{
                String like = (String)inpool.get("like");
                GridFsResource[] txtFiles = gridFsTemplate.getResources(like+"*");
                for (GridFsResource txtFile : txtFiles) {
                    System.out.println(txtFile.getFilename());
                }
            }
                break;
            case "expertfind":{
                String filename = (String)inpool.get("filename");
                Query query = new Query() ;
                //查询条件设置
                Criteria  criteria2 = GridFsCriteria.whereContentType();
                criteria2.is("image");
                query.addCriteria(criteria2);

                Criteria  criteria1 = GridFsCriteria.whereMetaData("realName");
                criteria1.is("MongDB搭建分片.docx");
                query.addCriteria(criteria1);

                Criteria  criteria = GridFsCriteria.whereFilename();
                criteria.is(filename) ;
                query.addCriteria(criteria);


                Sort.Order order = new Sort.Order(Sort.Direction.DESC, "uploadDate") ;
                Sort sort = new Sort(order) ;
                query.with(sort);


                List<GridFSDBFile> gridFils = gridFsTemplate.find(query) ;
                System.out.println(gridFils.size());

            }
        }

        return RETURN.SUCCESS;
    }
}
