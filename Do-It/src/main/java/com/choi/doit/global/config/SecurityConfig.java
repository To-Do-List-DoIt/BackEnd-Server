package com.choi.doit.global.config;

import com.choi.doit.domain.user.application.LoginDetailsService;
import com.choi.doit.domain.user.application.LoginService;
import com.choi.doit.domain.user.dao.UserRepository;
import com.choi.doit.global.error.handler.ExceptionHandlerFilter;
import com.choi.doit.global.util.ResponseUtil;
import com.choi.doit.global.util.jwt.JwtUtil;
import com.choi.doit.global.util.jwt.filter.JwtAuthenticationProcessingFilter;
import com.choi.doit.global.util.security.filter.JsonEmailPasswordAuthenticationFilter;
import com.choi.doit.global.util.security.handler.LoginFailureHandler;
import com.choi.doit.global.util.security.handler.LoginSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final LoginDetailsService loginDetailsService;
    private final LoginService loginService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ResponseUtil responseUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/api/v1/user/sign-up/**", "/api/v1/user/login/**", "/api/v1/user/guest/**").permitAll()
                                .anyRequest().authenticated());

        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), JsonEmailPasswordAuthenticationFilter.class);
        http.addFilterBefore(new ExceptionHandlerFilter(responseUtil), JwtAuthenticationProcessingFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(loginDetailsService);

        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(userRepository, responseUtil, jwtUtil);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler(responseUtil);
    }

    @Bean
    public JsonEmailPasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        JsonEmailPasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
                = new JsonEmailPasswordAuthenticationFilter(objectMapper, loginService);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtUtil, responseUtil);
    }
}
