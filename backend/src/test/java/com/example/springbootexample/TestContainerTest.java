package com.example.springbootexample;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestContainerTest extends AbstractTestContainer {

    @Test
    void canStartPostgresDB() {
        assertThat(POSTGRES_SQL_CONTAINER.isRunning()).isTrue();
        assertThat(POSTGRES_SQL_CONTAINER.isCreated()).isTrue();
    }
}
