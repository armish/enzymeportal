/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.data.dataconfig;

import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 *
 * @author joseph
 */
@Configuration
@OliverYard
@PropertySource({"classpath:ep-db-uzppub-oy.properties"})
public class OliverYardConfig implements iDataConfig {

    @Autowired
    private Environment env;

    //declare a datasource that has pooling capabilities
    @Bean
    @Override
    public DataSource dataSource() {
        try {

            OracleDataSource ds = new OracleConnectionPoolDataSource();

            String url = String.format("jdbc:oracle:thin:@%s:%s:%s",
                    env.getRequiredProperty("ep.db.host"), env.getRequiredProperty("ep.db.port"), env.getRequiredProperty("ep.db.instance"));

            ds.setURL(url);
            ds.setUser(env.getRequiredProperty("ep.db.username"));
            ds.setPassword(env.getRequiredProperty("ep.db.password"));

            ds.setConnectionCacheName("ep-connection-cache-01");
            ds.setImplicitCachingEnabled(true);

            Properties prop = new Properties();

            prop.setProperty("MinLimit", "5");     // the cache size is 5 at least 
            prop.setProperty("MaxLimit", "100");
            prop.setProperty("InitialLimit", "3"); // create 3 connections at startup

            return ds;
        } catch (IllegalStateException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
