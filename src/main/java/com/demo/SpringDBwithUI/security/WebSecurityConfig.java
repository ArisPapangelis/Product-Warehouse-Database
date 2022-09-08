package com.demo.SpringDBwithUI.security;

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

/**
 * Configuration class which configures Spring security for both the single page application
 * and the REST API, and sets up JDBC based database backed authentication.
 */
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    /**
     * Security configuration for the VAADIN single page application. The class is used ONLY when ApiSecurityConfig
     * is not applicable.
     *
     * @see ApiSecurityConfig
     */
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

            super.configure(http);

            setLoginView(http, LoginView.class);

        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            //web.ignoring().antMatchers("/VAADIN/**", "/favicon.ico", "/robots.txt", "/manifest.webmanifest", "/sw.js", "/offline-page.html", "/icons/**", "/images/**",
                    //"/frontend/**", "/webjars/**", "/h2-console/**", "/frontend-es5/**", "/frontend-es6/**", "/css/**");
            super.configure(web);
        }
    }

    /**
     * Security configuration for the REST API. The class is prioritised (Order annotation) for all URLs starting with /api. For all other URLs,
     * AppSecurityConfig is used.
     *
     * @see AppSecurityConfig
     */
    @Configuration
    @Order(1)
    public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .antMatcher("/api/**")
                    .csrf().disable()
                    .httpBasic().authenticationEntryPoint(new BadCredentialsAuthenticationEntryPoint()) //Custom exception is needed to handle invalid credentials.
                    .and()
                    .authorizeHttpRequests().anyRequest().permitAll();
        }
    }

    /**
     * Password encoder bean for hashing user passwords.
     *
     * @return The password encoder implementation to use.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Datasource bean used for persisting users in the database.
     *
     * @return The datasource to use for persisting new users.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/Products_DB");
        dataSource.setUsername( "demo_user" );
        dataSource.setPassword( "03081997" );
        return dataSource;
    }

    /**
     * Database populator which will execute the schema.sql script to create the schema needed for persisting new users.
     *
     * @return The DatabasePopulator object to be passed to DatabasePopulatorUtils.execute.
     */
    private DatabasePopulator createUserSchema() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        //databasePopulator.setContinueOnError(true);
        databasePopulator.addScript(new ClassPathResource("schema.sql"));
        return databasePopulator;
    }

    /**
     * UserDetailsManager bean which creates a built-in admin user, executes createUserSchema(), and returns a UserDetailsManager instance.
     *
     * @param dataSource The datasource for persisting new users.
     * @return The UserDetailsManager object through which new users are persisted.
     */
    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("demo_admin"))   //Normally would be hashed externally.
                .roles("ADMIN")
                .build();
        DatabasePopulatorUtils.execute(createUserSchema(), dataSource);
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.createUser(admin);
        return users;
    }

    /*
    //In-memory authentication
    @Bean
    public UserDetailsService userDetailsService() {

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

     */
}
