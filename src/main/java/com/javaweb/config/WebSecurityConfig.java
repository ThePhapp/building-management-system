package com.javaweb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.ApiSecurityErrorHandler;
import com.javaweb.security.CustomSuccessHandler;
import com.javaweb.service.impl.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                        .antMatchers("/admin/assets/**", "/admin/css/**", "/admin/font*/**", "/admin/js/**").permitAll()
                        .antMatchers("/web/**", "/login/**", "/img/**", "/resource/**", "/repository/**").permitAll()
                        .antMatchers("/", "/login", "/access-denied", "/error", "/trang-chu", "/gioi-thieu", "/san-pham", "/tin-tuc", "/lien-he").permitAll()
                        .antMatchers("/admin/user-*", "/admin/user/**").hasRole("ADMIN")
                        .antMatchers("/admin/building-edit*").hasAnyRole("ADMIN", "MANAGER")
                        .antMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .antMatchers("/api/user/profile", "/api/user/change-password").authenticated()
                        .antMatchers("/api/user/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/api/building/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                        .antMatchers("/api/building/**").hasAnyRole("ADMIN", "MANAGER")
                        .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").usernameParameter("j_username").passwordParameter("j_password").permitAll()
                .loginProcessingUrl("/j_spring_security_check")
                .successHandler(myAuthenticationSuccessHandler())
                .failureUrl("/login?incorrectAccount").and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/trang-chu").deleteCookies("JSESSIONID").permitAll()
                .and().exceptionHandling()
                .defaultAuthenticationEntryPointFor(apiSecurityErrorHandler(), new AntPathRequestMatcher("/api/**"))
                .accessDeniedHandler(apiSecurityErrorHandler()).and()
                .sessionManagement().maximumSessions(1).expiredUrl("/login?sessionTimeout");
    }

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
        return new CustomSuccessHandler();
    }

    @Bean
    public ApiSecurityErrorHandler apiSecurityErrorHandler() {
        return new ApiSecurityErrorHandler(objectMapper);
    }
}
