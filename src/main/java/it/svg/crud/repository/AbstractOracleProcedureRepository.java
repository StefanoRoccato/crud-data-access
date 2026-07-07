package it.svg.crud.repository;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import it.svg.crud.exception.CrudDataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.function.Function;

public abstract class AbstractOracleProcedureRepository {

    private static final Logger log = LoggerFactory.getLogger(AbstractOracleProcedureRepository.class);

    private final DataSource dataSource;
    private final MeterRegistry meterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    protected AbstractOracleProcedureRepository(DataSource dataSource,
                                                 MeterRegistry meterRegistry,
                                                 CircuitBreakerRegistry circuitBreakerRegistry) {
        this.dataSource = dataSource;
        this.meterRegistry = meterRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    protected <T> T execute(String sql, String procedureName,
                            SqlBinder binder, Function<CallableStatement, T> extractor) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry
                .circuitBreaker("crud." + procedureName.toLowerCase());

        Timer.Sample sample = Timer.start(meterRegistry);
        String outcome = "success";
        try {
            return CircuitBreaker
                    .decorateSupplier(circuitBreaker, () -> executeJdbc(sql, procedureName, binder, extractor))
                    .get();
        } catch (Exception ex) {
            outcome = "error";
            if (ex instanceof CrudDataAccessException cda) throw cda;
            throw new CrudDataAccessException("Stored procedure execution failed: " + procedureName, ex);
        } finally {
            String finalOutcome = outcome;
            sample.stop(Timer.builder("crud.procedure.execution")
                    .tag("procedure", procedureName.toLowerCase())
                    .tag("outcome", finalOutcome)
                    .register(meterRegistry));
            Counter.builder("crud.procedure.calls")
                    .tag("procedure", procedureName.toLowerCase())
                    .tag("outcome", finalOutcome)
                    .register(meterRegistry)
                    .increment();
        }
    }

    private <T> T executeJdbc(String sql, String procedureName,
                               SqlBinder binder, Function<CallableStatement, T> extractor) {
        long start = System.currentTimeMillis();
        try (Connection c = dataSource.getConnection(); CallableStatement cs = c.prepareCall(sql)) {
            binder.bind(cs);
            cs.execute();
            T result = extractor.apply(cs);
            log.debug("Stored procedure [{}] completed in {} ms", procedureName, System.currentTimeMillis() - start);
            return result;
        } catch (Exception ex) {
            log.error("Stored procedure [{}] execution failed", procedureName, ex);
            throw new CrudDataAccessException("Stored procedure execution failed: " + procedureName, ex);
        }
    }

    @FunctionalInterface
    protected interface SqlBinder {
        void bind(CallableStatement cs) throws Exception;
    }
}

