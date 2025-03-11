package com.tookscan.tookscan.security.config;

import com.tookscan.tookscan.core.constant.Constants;
import com.tookscan.tookscan.core.utility.JsonWebTokenUtil;
import com.tookscan.tookscan.security.application.service.CustomOauth2UserDetailService;
import com.tookscan.tookscan.security.application.usecase.AuthenticateJsonWebTokenUseCase;
import com.tookscan.tookscan.security.filter.ExceptionFilter;
import com.tookscan.tookscan.security.filter.GlobalLoggerFilter;
import com.tookscan.tookscan.security.filter.JsonWebTokenAuthenticationFilter;
import com.tookscan.tookscan.security.filter.RedirectUrlFilter;
import com.tookscan.tookscan.security.handler.common.DefaultAccessDeniedHandler;
import com.tookscan.tookscan.security.handler.common.DefaultAuthenticationEntryPoint;
import com.tookscan.tookscan.security.handler.login.DefaultLoginFailureHandler;
import com.tookscan.tookscan.security.handler.login.DefaultLoginSuccessHandler;
import com.tookscan.tookscan.security.handler.login.Oauth2FailureHandler;
import com.tookscan.tookscan.security.handler.login.Oauth2SuccessHandler;
import com.tookscan.tookscan.security.handler.logout.DefaultLogoutProcessHandler;
import com.tookscan.tookscan.security.handler.logout.DefaultLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final DefaultLoginSuccessHandler defaultLoginSuccessHandler;
    private final DefaultLoginFailureHandler defaultLoginFailureHandler;

    private final DefaultLogoutProcessHandler defaultLogoutProcessHandler;
    private final DefaultLogoutSuccessHandler defaultLogoutSuccessHandler;

    private final Oauth2SuccessHandler oauth2SuccessHandler;
    private final Oauth2FailureHandler oauth2FailureHandler;
    private final CustomOauth2UserDetailService customOauth2UserDetailService;

    private final DefaultAccessDeniedHandler defaultAccessDeniedHandler;
    private final DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint;

    private final AuthenticateJsonWebTokenUseCase authenticateJsonWebTokenUseCase;

    private final JsonWebTokenUtil jsonWebTokenUtil;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(Constants.NO_NEED_AUTH_URLS.toArray(String[]::new)).permitAll()
                        .requestMatchers(Constants.USER_URLS.toArray(String[]::new)).hasRole("USER")
                        .requestMatchers(Constants.ADMIN_URLS.toArray(String[]::new)).hasRole("ADMIN")
                        .requestMatchers(Constants.COMMON_URLS.toArray(String[]::new)).hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oauth2SuccessHandler)
                        .failureHandler(oauth2FailureHandler)
                        .userInfoEndpoint(it -> it.userService(customOauth2UserDetailService))
                )

                .formLogin(configurer -> configurer
                        .loginPage("/login")
                        .loginProcessingUrl("/v1/auth/login")
                        .usernameParameter("serial_id")
                        .passwordParameter("password")
                        .successHandler(defaultLoginSuccessHandler)
                        .failureHandler(defaultLoginFailureHandler)
                )

                .logout(configurer -> configurer
                        .logoutUrl("/v1/auth/logout")
                        .addLogoutHandler(defaultLogoutProcessHandler)
                        .logoutSuccessHandler(defaultLogoutSuccessHandler)
                )

                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(defaultAccessDeniedHandler)
                        .authenticationEntryPoint(defaultAuthenticationEntryPoint)
                )

                .addFilterBefore(
                        new JsonWebTokenAuthenticationFilter(
                                authenticateJsonWebTokenUseCase,
                                jsonWebTokenUtil
                        ),
                        LogoutFilter.class
                )

                .addFilterBefore(
                        new ExceptionFilter(),
                        JsonWebTokenAuthenticationFilter.class
                )

                .addFilterBefore(
                        new GlobalLoggerFilter(),
                        ExceptionFilter.class
                )

                .addFilterBefore(
                        new RedirectUrlFilter(),
                        GlobalLoggerFilter.class
                )

                .getOrBuild();
    }
}

