package org.sumbootFrame.data.dao;

/**
 * Created by thinkpad on 2017/9/22.
 */

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

@Configuration
@PropertySource({
        "classpath:properties/datasource.properties",
        "classpath:self-properties/datasource-self.properties"
})
@EnableTransactionManagement
@MapperScan(basePackages = "org.sumbootFrame.data.dao.secondary.**", sqlSessionTemplateRef  = "secondarySqlSessionTemplate")
public class DataSource2Config {
    /**
     * 数据源配置对象
     * Primary 表示默认的对象，Autowire可注入，不是默认的得明确名称注入
     * @return
     */
    @Bean
    @ConfigurationProperties("secondary.datasource")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "secondary.datasource")
    public DataSource secondaryDataSource() {
        /**
         * MySql数据库驱动 实现 XADataSource接口
         */
        DruidXADataSource xaDataSource = new DruidXADataSource();
        if(secondaryDataSourceProperties().getUrl().equals("")
                ||secondaryDataSourceProperties().getUsername().equals("")
                ||secondaryDataSourceProperties().getPassword().equals("")
                ||secondaryDataSourceProperties().getDriverClassName().equals("")){
            xaDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
            atomikosDataSourceBean.setXaDataSource(xaDataSource);
            System.err.println("数据源2注入.....空");
            return atomikosDataSourceBean;
        }else{
            xaDataSource.setUrl(secondaryDataSourceProperties().getUrl());
            xaDataSource.setUsername(secondaryDataSourceProperties().getUsername());
            xaDataSource.setPassword(secondaryDataSourceProperties().getPassword());
            xaDataSource.setDriverClassName(secondaryDataSourceProperties().getDriverClassName());
            Map<String,String> propertiesMap = secondaryDataSourceProperties().getXa().getProperties();
            xaDataSource.setMaxActive(Integer.parseInt(propertiesMap.get("maxActive").toString()));
            xaDataSource.setInitialSize(Integer.parseInt(propertiesMap.get("initialSize").toString()));
            xaDataSource.setMinIdle(Integer.parseInt(propertiesMap.get("minIdle").toString()));
            xaDataSource.setMaxWait(Integer.parseInt(propertiesMap.get("maxWait").toString()));
            xaDataSource.setTimeBetweenEvictionRunsMillis(Integer.parseInt(propertiesMap.get("timeBetweenEvictionRunsMillis").toString()));
//            xaDataSource.setUseUnfairLock();//非公平锁 默认不打开

            xaDataSource.setValidationQuery(propertiesMap.get("validationQuery").toString());
            xaDataSource.setTestOnBorrow(Boolean.valueOf(propertiesMap.get("testOnBorrow")));
            xaDataSource.setTestOnReturn(Boolean.valueOf(propertiesMap.get("testOnReturn")));
            xaDataSource.setPoolPreparedStatements(Boolean.valueOf(propertiesMap.get("poolPreparedStatements")));
            xaDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(propertiesMap.get("maxPoolPreparedStatementPerConnectionSize").toString()));
            try {
                xaDataSource.setFilters(propertiesMap.get("filters").toString());
                xaDataSource.init();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            /**
             * 设置分布式-- 主数据源
             */
            AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
            atomikosDataSourceBean.setXaDataSource(xaDataSource);
            atomikosDataSourceBean.setUniqueResourceName("secondaryDataSource");
            atomikosDataSourceBean.setXaDataSourceClassName(DruidXADataSource.class.getName());

            Properties properties = new Properties();
            for(Map.Entry entry :propertiesMap.entrySet()){
                properties.put(entry.getKey(),entry.getValue());
            }
            atomikosDataSourceBean.setXaProperties(properties);
            System.err.println("数据源2注入成功.....");
            return atomikosDataSourceBean;
        }
    }

    @Bean(name = "secondarySqlSessionFactory")
    @ConfigurationProperties(prefix = "secondary.datasource")
    public SqlSessionFactory secondarySqlSessionFactory(@Qualifier("secondaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/secondary/*.xml"));
        return bean.getObject();
    }

//    @Bean(name = "secondaryTransactionManager")
//    @ConfigurationProperties(prefix = "secondary.datasource")
//    public DataSourceTransactionManager secondaryTransactionManager(@Qualifier("secondaryDataSource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }

    @Bean(name = "secondarySqlSessionTemplate")
    @ConfigurationProperties(prefix = "secondary.datasource")
    public SqlSessionTemplate secondarySqlSessionTemplate(@Qualifier("secondarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
