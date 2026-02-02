package ch.fullstack.dalzana.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * Test-Konfiguration für Unit Tests
 * Ermöglicht das Ausführen von Tests ohne MySQL-Verbindung
 */
@TestConfiguration
public class TestDatabaseConfig {
    
    @Bean
    @Primary
    public DataSource testDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false")
                .username("sa")
                .password("")
                .build();
    }
}
