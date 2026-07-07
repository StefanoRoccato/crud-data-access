package it.svg.crud.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Spring Actuator health indicator for the crud-data-access Oracle datasource.
 *
 * <p>Activated only when {@code crud.db.tns} is configured (i.e., when the library
 * manages its own Oracle datasource). When the consuming application provides its own
 * datasource (e.g., H2 for tests, jOOQ datasource), this indicator is not registered.
 */
@Component("crudOracle")
@ConditionalOnProperty(prefix = "crud.db", name = "tns")
public class CrudOracleHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CrudOracleHealthIndicator(DataSource dataSource, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.dataSource = dataSource;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public Health health() {
        try (Connection c = dataSource.getConnection(); Statement st = c.createStatement()) {
            st.execute("SELECT 1 FROM DUAL");
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("reason", "Oracle connectivity check failed")
                    .build();
        }

        Health.Builder builder = Health.up().withDetail("db", "Oracle reachable");

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            CircuitBreaker.State state = cb.getState();
            builder.withDetail("circuitBreaker." + cb.getName(), state.name());
            if (state == CircuitBreaker.State.OPEN) {
                builder.down();
            }
        });

        return builder.build();
    }
}
