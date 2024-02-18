package com.example.config;

import com.example.util.DBConnectionProvider;
import com.example.util.MigrationExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
@PropertySource(value = "classpath:application.yaml")
public class AppConfig {
    private final MigrationExecutor migrationExecutor;
    private final DBConnectionProvider connectionProvider;

    @Autowired
    public AppConfig(MigrationExecutor migrationExecutor, DBConnectionProvider connectionProvider) {
        this.migrationExecutor = migrationExecutor;
        this.connectionProvider = connectionProvider;
        init();
    }

    private void init() {
        migrationExecutor.execute(connectionProvider);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yaml"));
        configurer.setProperties(yaml.getObject());
        return configurer;
    }
}
