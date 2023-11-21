package com.hellocorp.example.iam.application.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableConfigurationProperties
@MapperScan(basePackages = {"com.hellocorp.example.iam.infra.persist.dao"})
public class IamDataSourceConfig {

    @Value("${spring.datasource.schemaLocation:#{null}}")
    private String schemaLocation;

    @Bean
    SqlDataSourceScriptDatabaseInitializer iamDatabaseInitializer(DataSource dataSource) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        if (schemaLocation == null) {
            settings.setMode(DatabaseInitializationMode.NEVER);
        } else {
            settings.setMode(DatabaseInitializationMode.ALWAYS);
            settings.setSchemaLocations(List.of(schemaLocation));
        }
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, settings);
    }

}
