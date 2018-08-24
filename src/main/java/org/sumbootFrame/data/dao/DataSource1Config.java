package org.sumbootFrame.data.dao;

/**
 * Created by thinkpad on 2017/9/22.
 */

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;



@Configuration
@PropertySource({
        "classpath:properties/datasource.properties",
        "classpath:self-properties/datasource-self.properties"
})
@EnableTransactionManagement
@MapperScan(basePackages = "org.sumbootFrame.data.dao.primary.**", sqlSessionTemplateRef  = "primarySqlSessionTemplate")
public class DataSource1Config {
    /**
     * 数据源配置对象
     * Primary 表示默认的对象，Autowire可注入，不是默认的得明确名称注入
     * @return
     */
    @Bean
    @Primary
    @ConfigurationProperties("primary.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "primary.datasource")
    @Primary
    public DataSource DataSource() {
        /**
         * MySql数据库驱动 实现 XADataSource接口
         */
//        MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
//        mysqlXaDataSource.setUrl(primaryDataSourceProperties().getUrl());
//        mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
//        mysqlXaDataSource.setPassword(primaryDataSourceProperties().getPassword());
//        mysqlXaDataSource.setUser(primaryDataSourceProperties().getUsername());
//        mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);

        DruidXADataSource xaDataSource = new DruidXADataSource();
        xaDataSource.setUrl(primaryDataSourceProperties().getUrl());
        xaDataSource.setUsername(primaryDataSourceProperties().getUsername());
        xaDataSource.setPassword(primaryDataSourceProperties().getPassword());



        /**
         * 设置分布式-- 主数据源
         */
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(xaDataSource);
        atomikosDataSourceBean.setUniqueResourceName("PrimaryDB");
//        atomikosDataSourceBean.setMinPoolSize();
//        atomikosDataSourceBean.setMaxPoolSize();
//
//        xaDataSource.setMaxLifetime();
//        xaDataSource.setBorrowConnectionTimeout();
//        xaDataSource.setLoginTimeout();
//        xaDataSource.setMaintenanceInterval();
//        xaDataSource.setMaxIdleTime();
//        xaDataSource.setTestQuery();
//        xaDataSource.setXaProperties();
        System.err.println("主数据源注入成功.....");
        return atomikosDataSourceBean;

//        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "primarySqlSessionFactory")
    @ConfigurationProperties(prefix = "primary.datasource")
    @Primary
    public SqlSessionFactory SqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/primary/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "primaryTransactionManager")
    @ConfigurationProperties(prefix = "primary.datasource")
    @Primary
    public DataSourceTransactionManager TransactionManager(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "primarySqlSessionTemplate")
    @ConfigurationProperties(prefix = "primary.datasource")
    @Primary
    public SqlSessionTemplate SqlSessionTemplate(@Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
