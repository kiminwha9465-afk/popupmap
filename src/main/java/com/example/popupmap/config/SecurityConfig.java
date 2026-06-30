package com.example.popupmap.config;

import com.example.popupmap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();
        delegate.setDefaultTargetUrl("/");
        return (request, response, auth) -> {
            String redirect = request.getParameter("redirect");
            if (redirect != null && !redirect.isBlank()
                    && redirect.startsWith("/") && !redirect.startsWith("//")) {
                response.sendRedirect(request.getContextPath() + redirect);
            } else {
                delegate.onAuthenticationSuccess(request, response, auth);
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider(userService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/stores", "/store/**",
                                 "/news", "/news/**",
                                 "/login", "/signup",
                                 "/css/**", "/js/**", "/images/**", "/uploads/**",
                                 "/api/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(loginSuccessHandler())
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .headers(h -> h.frameOptions(f -> f.sameOrigin())); // H2 콘솔
        return http.build();
    }
}
