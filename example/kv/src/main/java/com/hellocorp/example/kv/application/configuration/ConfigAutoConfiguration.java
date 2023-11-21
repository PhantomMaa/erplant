package com.hellocorp.example.kv.application.configuration;

import javax.sql.DataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@EnableConfigurationProperties
@ComponentScan(
    basePackages = {
            "com.hellocorp.example.kv.domain.repository",
            "com.hellocorp.example.kv.domain.converter",
            "com.hellocorp.example.kv.domain.service.impl"
    }
)
@MapperScan(
    basePackages = {"com.hellocorp.example.kv.infra.persist.dao"}
)
public class ConfigAutoConfiguration {

    @Bean
    public TransactionTemplate configTransactionTemplate(DataSource dataSource) {
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        return new TransactionTemplate(transactionManager);
    }

}
