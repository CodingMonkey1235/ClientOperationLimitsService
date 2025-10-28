package org.example.config;

import org.example.repository.ClientsLimitsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.example")
public class ApplicationConfiguration {

    @Bean
    public ClientsLimitsConfiguration clientsLimitsConfiguration(ClientsLimitsRepository clientsLimitsRepository) {
        return new ClientsLimitsConfiguration();
    }
}
