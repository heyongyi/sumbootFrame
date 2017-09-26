package org.sumbootFrame.data.dao;

/**
 * Created by thinkpad on 2017/9/22.
 */

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
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

@Configuration
@PropertySource({
        "classpath:datasource.properties",
        "classpath:static/property/datasource-self.properties"
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
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
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
