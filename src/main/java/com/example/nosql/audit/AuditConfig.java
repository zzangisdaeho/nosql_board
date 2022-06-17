package com.example.nosql.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class AuditConfig{

    @Bean
    public AuditorAware<String> auditProvider(){
        return () -> Optional.of("auditor");
    }
}