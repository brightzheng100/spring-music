package org.cloudfoundry.samples.music.config.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile({"postgres", "!cloud"})
public class PostgresLocalDataSourceConfig extends AbstractDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        return createBasicDataSource("jdbc:postgresql://localhost/music",
                "org.postgresql.Driver", "postgres", "postgres");
    }

}