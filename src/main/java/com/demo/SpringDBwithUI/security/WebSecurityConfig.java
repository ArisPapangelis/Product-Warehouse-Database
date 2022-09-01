package com.demo.SpringDBwithUI.security;

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Configuration
    public static class AppSecurityConfig extends VaadinWebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Delegating the responsibility of general configurations
            // of http security to the super class. It is configuring
            // the followings: Vaadin's CSRF protection by ignoring
            // framework's internal requests, default request cache,
            // ignoring public views annotated with @AnonymousAllowed,
            // restricting access to other views/endpoints, and enabling
            // ViewAccessChecker authorization.
            // You can add any possible extra configurations of your own
            // here (the following is just an example):

            // http.rememberMe().alwaysRemember(false);

            super.configure(http);

            setLoginView(http, LoginView.class);

        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            //web.ignoring().antMatchers("/VAADIN/**", "/favicon.ico", "/robots.txt", "/manifest.webmanifest", "/sw.js", "/offline-page.html", "/icons/**", "/images/**",
                    //"/frontend/**", "/webjars/**", "/h2-console/**", "/frontend-es5/**", "/frontend-es6/**");
            super.configure(web);
        }
    }


    @Configuration
    @Order(1)
    public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .antMatcher("/api/**")
                    .csrf().disable()
                    .httpBasic().authenticationEntryPoint(new BadCredentialsAuthenticationEntryPoint())
                    .and()
                    .authorizeHttpRequests().anyRequest().permitAll();
        }
    }

    @Bean
    public UserDetailsService userDetailsService () {

        User.UserBuilder users = User.withDefaultPasswordEncoder();

        UserDetails user = users
                .username("demo")
                .password("demo_user")
                .roles("USER")
                .build();

        UserDetails admin = users
                .username("admin")
                .password("demo_admin")
                .roles("ADMIN")
                .build();

        System.err.println(admin.getPassword());
        return new InMemoryUserDetailsManager(user, admin);
    }
}
