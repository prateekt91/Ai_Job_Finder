package com.jobfinder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Manyata Tech Park - Company & Job Finder API")
                        .description("REST API to discover tech companies and live job postings " +
                                "from Manyata Embassy Business Park, Bengaluru. " +
                                "Company data is scraped from public sources and job listings are " +
                                "fetched from LinkedIn. Both are refreshed automatically every week.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Job Finder")
                                .url("https://github.com/jobfinder")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development")));
    }
}
