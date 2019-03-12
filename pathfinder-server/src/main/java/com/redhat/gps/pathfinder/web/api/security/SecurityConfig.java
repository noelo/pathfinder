package com.redhat.gps.pathfinder.web.api.security;

/*-
 * #%L
 * Pathfinder
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2018 RedHat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtAuthenticationEntryPoint authEntryPoint;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/i18n/**")
                .antMatchers("/content/**")
                .antMatchers("/swagger-ui/index.html")
                .antMatchers("/test/**")
                .antMatchers("/*.jsp")
                .antMatchers("/pathfinder/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        boolean authentication = true;
        if (authentication) {
            http.cors();
            http.addFilterAfter(new JwtAuthorizationTokenFilter(userDetailsService, jwtTokenUtil, "Authorization"), BasicAuthenticationFilter.class);
            http.authorizeRequests()
                    .antMatchers("/docs**").permitAll()
                    .antMatchers("/pathfinder/**").permitAll()
                    .antMatchers("/auth").permitAll()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/management/health").permitAll()
                    .antMatchers("/api/pathfinder/docs**").permitAll()
                    .antMatchers("/api/pathfinder/survey").permitAll()
                    .antMatchers("/api/pathfinder/login").permitAll()
                    .antMatchers("/api/pathfinder/**").access("hasAnyAuthority('SUPER','ADMIN','USER')");
        }
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
