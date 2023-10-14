package com.tsyhanok.restapi.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@Configuration
public class DbConfig {

    @Profile("dev")
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp","-tcpAllowOthers","-tcpPort","9092");
    }
}


//Запускаем приложение и, после этого, подключаем БД в IDE
//
//url: jdbc:h2:tcp://localhost:9092/mem:testdb
//user: sa
//password: