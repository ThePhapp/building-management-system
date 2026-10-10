package com.javaweb.config;

import com.opensymphony.module.sitemesh.filter.PageFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@Configuration
public class SiteMeshConfig {

    @Bean
    public FilterRegistrationBean<PageFilter> siteMeshFilter() {
        FilterRegistrationBean<PageFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PageFilter());
        registration.setName("siteMeshFilter");
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
        return registration;
    }
}
