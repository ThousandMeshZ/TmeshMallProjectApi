//package com.tmesh.tmeshmall.product.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import io.seata.rm.datasource.DataSourceProxy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//
//import javax.sql.DataSource;
//
//
///**
// * @Description:
// * @Created: with IntelliJ IDEA.
// * @author: TMesh
// * @createTime: 2024-01-06 11:57
// **/
//
//@Configuration
//public class MySeataConfig {
//
//    @Autowired
//    DataSourceProperties dataSourceProperties;
//
//
//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
//
//        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(dataSourceProperties.getName())) {
//            dataSource.setPoolName(dataSourceProperties.getName());
//        }
//
//        return new DataSourceProxy(dataSource);
//    }
//
//}
