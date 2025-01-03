package com.pmk.notif.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "notifEntityManagerFactory",
        transactionManagerRef = "notifTransactionManager",
        basePackages = { "com.pmk.notif.repositories.pubsubs"})
public class NotifDBConfig {

    @Bean(name="NotifProps")
    @ConfigurationProperties("spring.notif.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean(name="notifDatasource")
    @ConfigurationProperties(prefix = "spring.notif.datasource")
    public DataSource datasource(@Qualifier("NotifProps") DataSourceProperties properties){
        return properties.initializeDataSourceBuilder().build();
    }


    @Bean(name="notifEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
            (EntityManagerFactoryBuilder builder,
             @Qualifier("notifDatasource") DataSource dataSource){
        return builder.dataSource(dataSource)
                .packages("com.pmk.notif.models.pubsubs")
                .persistenceUnit("notif_pubsubs").build();
    }

    @Bean(name = "notifTransactionManager")
    @ConfigurationProperties("spring.notif.jpa")
    public PlatformTransactionManager transactionManager(
            @Qualifier("notifEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
