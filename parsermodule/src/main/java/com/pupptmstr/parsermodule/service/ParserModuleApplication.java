package com.pupptmstr.parsermodule.service;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@SpringBootApplication
@Configuration
public class ParserModuleApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ParserModuleApplication.class, args);
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(2048));
        factory.setMaxRequestSize(DataSize.ofMegabytes(2048));
        return factory.createMultipartConfig();
    }
}