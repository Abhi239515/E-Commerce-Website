package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.ecom.service.impl.UserDetailsServiceImpl;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Autowired
	@Lazy
	private AuthFailureHandlerImpl authenticationFailureHandler;

    @Autowired
    private UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable())
	        .cors(cors -> cors.disable())
	        .authorizeHttpRequests(req -> req
	            .requestMatchers("/uploads/**").permitAll()          // Allow images without login
	            .requestMatchers("/signin", "/css/**", "/js/**").permitAll()  // Allow login page and static resources
	            .requestMatchers("/user/**").hasRole("USER")          // User role protected
	            .requestMatchers("/admin/**").hasRole("ADMIN")        // Admin role protected
	            .anyRequest().authenticated()                          // All others require login
	        )
	        .formLogin(form -> form
	            .loginPage("/signin")
	            .loginProcessingUrl("/login")
	            .failureHandler(authenticationFailureHandler)
	            .successHandler(authenticationSuccessHandler)
	        )
	        .logout(logout -> logout.permitAll());

	    return http.build();
	}


	
}
