package com.hellocorp.example.iam;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableConfigurationProperties
@Slf4j
public class TestContainerConfig {

    private static final String schemaLocation = "classpath:ddl/V1.0__iam_base.sql";

    @Container
    private static final MySQLContainer mysql = (MySQLContainer) (new MySQLContainer("mysql:8.0.32"))
            .withDatabaseName("test")
            .withCommand("mysqld --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci");

    @PostConstruct
    public void startTestContainer() {
        mysql.start();
    }

    @PreDestroy
    public void stopTestContainer() {
        if (mysql.isRunning()) {
            mysql.stop();
        }
    }

    @Bean
    public DataSource dataSource() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl(mysql.getJdbcUrl());
        properties.setUsername(mysql.getUsername());
        properties.setPassword(mysql.getPassword());
        properties.setDriverClassName(mysql.getDriverClassName());
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer configDatabaseInitializer(DataSource dataSource) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        settings.setMode(DatabaseInitializationMode.ALWAYS);
        settings.setSchemaLocations(List.of(schemaLocation));
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, settings);
    }

}
