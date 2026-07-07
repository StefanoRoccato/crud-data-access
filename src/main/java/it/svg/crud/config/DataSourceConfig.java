package it.svg.crud.config;

import oracle.jdbc.pool.OracleDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Creates a dedicated Oracle DataSource for the crud-data-access library.
 *
 * <p>Activated ONLY when {@code crud.db.tns} is explicitly configured in
 * {@code application.yml}. This prevents the library from interfering with
 * consuming applications that already provide their own datasource
 * (e.g., jOOQ + H2 in test mode).
 */
@Configuration
@ConditionalOnProperty(prefix = "crud.db", name = "tns")
@EnableConfigurationProperties(CrudDbProperties.class)
public class DataSourceConfig {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
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
