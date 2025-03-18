package com.backend.board_service.config;

import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

    @Bean
    public DataSource dataSource() {
        Dotenv dotenv = Dotenv.configure()
                .directory("C:/dev/spring-boot/board-service/board-service")
                .load();

        //System.out.println("DB_URL: " + dotenv.get("DB_URL"));

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(dotenv.get("DB_URL"));
        dataSource.setUsername(dotenv.get("DB_USER"));
        dataSource.setPassword(dotenv.get("DB_PASSWORD"));
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        return dataSource;
    }
}
