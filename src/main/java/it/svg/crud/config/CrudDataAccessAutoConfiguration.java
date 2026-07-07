package it.svg.crud.config;

import it.svg.crud.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Spring Boot auto-configuration for the crud-data-access library.
 *
 * <p>Activates component scanning of the {@code it.svg.crud} package so that all
 * generated CRUD module services ({@code IndividualeService}, …) and their repository
 * implementations are registered as Spring beans in the consuming application context.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>{@link DataSourceConfig#dataSource} is {@code @ConditionalOnMissingBean(DataSource.class)},
 *       so it reuses the consuming application's primary datasource when one is already present.</li>
 *   <li>CRUD repository beans carry explicit {@code @Repository("crud<Name>Repository")} names
 *       to avoid clashes with same-named repositories in the consuming microservice.</li>
 *   <li>{@link GlobalExceptionHandler} is excluded from the component scan and registered only
 *       when the consuming application has no {@code @RestControllerAdvice} of its own.</li>
 * </ul>
 *
 * <p>Registration: {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 */
@AutoConfiguration
@ComponentScan(
        basePackages = "it.svg.crud",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = GlobalExceptionHandler.class
        )
)
public class CrudDataAccessAutoConfiguration {

    /**
     * Registers the crud {@link GlobalExceptionHandler} only when the consuming application
     * does not provide its own {@code @RestControllerAdvice}. Uses a distinct bean name
     * {@code "crudGlobalExceptionHandler"} to avoid conflicts if overriding is enabled.
     */
    @Bean("crudGlobalExceptionHandler")
    @ConditionalOnMissingBean(annotation = RestControllerAdvice.class)
    public GlobalExceptionHandler crudGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
