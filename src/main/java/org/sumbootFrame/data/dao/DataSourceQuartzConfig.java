package org.sumbootFrame.data.dao;

/**
 * Created by thinkpad on 2017/9/22.
 */

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

@Configuration
@PropertySource({
        "classpath:datasource.properties",
        "classpath:static/property/datasource-self.properties"
})
@EnableTransactionManagement
@MapperScan(basePackages = "org.sumbootFrame.data.dao.quartz.**", sqlSessionTemplateRef  = "quartzSqlSessionTemplate")
public class DataSourceQuartzConfig {
    /**
     * 数据源配置对象
     * quartz 表示默认的对象，Autowire可注入，不是默认的得明确名称注入
     * @return
     */
    @Bean
    @ConfigurationProperties("quartz.datasource")
    public DataSourceProperties quartzDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Bean(name = "quartzDataSource")
    @ConfigurationProperties(prefix = "quartz.datasource")
    public DataSource DataSource() {
        return quartzDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "quartzSqlSessionFactory")
    @ConfigurationProperties(prefix = "quartz.datasource")
    public SqlSessionFactory SqlSessionFactory(@Qualifier("quartzDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/quartz/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "quartzTransactionManager")
    @ConfigurationProperties(prefix = "quartz.datasource")
    public DataSourceTransactionManager TransactionManager(@Qualifier("quartzDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "quartzSqlSessionTemplate")
    @ConfigurationProperties(prefix = "quartz.datasource")
    public SqlSessionTemplate SqlSessionTemplate(@Qualifier("quartzSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
