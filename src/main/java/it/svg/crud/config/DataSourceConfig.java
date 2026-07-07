package it.svg.crud.config;

import oracle.jdbc.pool.OracleDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableConfigurationProperties(CrudDbProperties.class)
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(CrudDbProperties props) throws SQLException {
        if (props.getTnsAdmin() != null && !props.getTnsAdmin().isBlank()) {
            System.setProperty("oracle.net.tns_admin", props.getTnsAdmin());
        }
        OracleDataSource ds = new OracleDataSource();
        ds.setUser(props.getUser());
        ds.setPassword(props.getPassword());
        ds.setURL("jdbc:oracle:thin:@" + props.getTns());
        return ds;
    }
}
