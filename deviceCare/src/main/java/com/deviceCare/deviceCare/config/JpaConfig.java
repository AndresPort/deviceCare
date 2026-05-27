package com.deviceCare.deviceCare.config;

import com.deviceCare.deviceCare.common.audit.AuditAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditAware")
public class JpaConfig {

    @Bean
    public AuditAware auditAware() {
        return new AuditAware();
    }
}