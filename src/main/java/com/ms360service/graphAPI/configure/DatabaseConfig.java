package com.ms360service.graphAPI.configure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@Import(DatabaseSetting.class)
@EnableTransactionManagement
public class DatabaseConfig {
    @Autowired
    private DatabaseSetting setting;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        LOGGER.info("DB setup: \n{}", setting.printout());
        return
                DataSourceBuilder.create()
                        .driverClassName(setting.getDriverClassName())
                        .username(setting.getUsername())
                        .password(setting.getPassword())
                        .url(setting.getUrl())
                        .build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager getTransactionManager() {
        DataSourceTransactionManager txManager = new DataSourceTransactionManager();
        txManager.setDataSource(dataSource());
        return txManager;
    }

}
