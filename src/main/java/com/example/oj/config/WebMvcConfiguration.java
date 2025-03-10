package com.example.oj.config;

//import com.example.oj.interceptor.JwtTokenAdminInterceptor;

import com.example.oj.interceptor.JwtTokenAdminInterceptor;
import com.example.oj.interceptor.LoggingFilter;
import com.example.oj.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Configuration class, registers web layer related components
 */
@Configuration
@Slf4j
@EnableTransactionManagement
@EnableSpringDataWebSupport
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

	private final JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
	private final UserRepository userRepository;

	@Value("${frontend.address}")
	private String frontendAddress;

//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/swagger-ui/**")
//				.addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
///*		registry.addResourceHandler("/swagger-ui.html")
//				.addResourceLocations("classpath:/META-INF/resources/");*/
//		registry.addResourceHandler("/v3/api-docs/**")
//				.addResourceLocations("classpath:/META-INF/resources/");
//	}

	public WebMvcConfiguration(JwtTokenAdminInterceptor jwtTokenAdminInterceptor,
							   UserRepository userRepository) {
		this.jwtTokenAdminInterceptor = jwtTokenAdminInterceptor;
		this.userRepository = userRepository;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> userRepository.findByUsername(username);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins(frontendAddress)
				.allowedMethods("GET", "POST", "PUT", "DELETE")
				.allowedHeaders("*")
				.allowCredentials(true);
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public FilterRegistrationBean<LoggingFilter> loggingFilterRegistration(LoggingFilter loggingFilter) {
		FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(loggingFilter);
		registrationBean.addUrlPatterns("/*"); // Apply to all URLs
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // Set highest precedence
		return registrationBean;
	}

}
