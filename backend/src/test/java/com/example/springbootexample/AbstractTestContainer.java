package com.example.springbootexample;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestContainer {

    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway.configure().dataSource(
                POSTGRES_SQL_CONTAINER.getJdbcUrl(),
                POSTGRES_SQL_CONTAINER.getUsername(),
                POSTGRES_SQL_CONTAINER.getPassword()
        ).load();

        flyway.migrate();
    }


    @Container
    protected static final PostgreSQLContainer<?> POSTGRES_SQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("amigos-dao-unit-test")
                    .withUsername("amigos-code")
                    .withPassword("password");

    @DynamicPropertySource
    private static void registryDataSourceProperty(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                POSTGRES_SQL_CONTAINER::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                POSTGRES_SQL_CONTAINER::getUsername
        );

        registry.add(
                "spring.datasource.password",
                POSTGRES_SQL_CONTAINER::getPassword
        );
    }

    private static DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(POSTGRES_SQL_CONTAINER.getDriverClassName())
                .url(POSTGRES_SQL_CONTAINER.getJdbcUrl())
                .username(POSTGRES_SQL_CONTAINER.getUsername())
                .password(POSTGRES_SQL_CONTAINER.getPassword())
                .build();
    }

    protected static JdbcTemplate getJDBCTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    protected final Faker FAKER = new Faker();
}
