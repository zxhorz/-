/*
 * Project Name: springbootdemo
 * File Name: DruidConfigration.java
 * Class Name: DruidConfigration
 *
 * Copyright 2014 Hengtian Software Inc
 *
 * Licensed under the Hengtiansoft
 *
 * http://www.hengtiansoft.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zxh.dormMG.config;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 *  
 * 描述：如果不使用代码手动初始化DataSource的话，监控界面的SQL监控会没有数据("是spring boot的bug???") 
 */
@Configuration
public class DruidConfigration {  
    @Value("${spring.datasource.url}")
    private String dbUrl;  
    @Value("${spring.datasource.username}")
    private String username;  
    @Value("${spring.datasource.password}")
    private String password;  
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;  
    @Value("${spring.datasource.initialSize}")
    private int initialSize;  
    @Value("${spring.datasource.minIdle}")
    private int minIdle;  
    @Value("${spring.datasource.maxActive}")
    private int maxActive;  
    @Value("${spring.datasource.maxWait}")
    private int maxWait;  
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;  
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;  
    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;  
    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;  
    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;  
    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;  
    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatements;
    @Value("${spring.datasource.connectionProperties}")
    private String connectionProperties;

    @Bean     //声明其为Bean实例
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
    public DataSource dataSource(){  
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(this.dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
  
        //configuration  
        datasource.setInitialSize(initialSize);  
        datasource.setMinIdle(minIdle);  
        datasource.setMaxActive(maxActive);  
        datasource.setMaxWait(maxWait);  
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);  
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);  
        datasource.setValidationQuery(validationQuery);  
        datasource.setTestWhileIdle(testWhileIdle);  
        datasource.setTestOnBorrow(testOnBorrow);  
        datasource.setTestOnReturn(testOnReturn);  
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setConnectionProperties(connectionProperties);
        return datasource;  
    }

    @Bean
    public WallFilter wallFilter(){
        WallFilter wallFilter=new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    @Bean
    public WallConfig wallConfig(){
        WallConfig config =new WallConfig();
        config.setMultiStatementAllow(true);//允许一次执行多条语句
        return config;
    }

    @Bean
    public StatFilter statFilter(){
        return new StatFilter();
    }


    @Bean
    public Slf4jLogFilter slf4jLogFilter(){
        return new Slf4jLogFilter();
    }

}