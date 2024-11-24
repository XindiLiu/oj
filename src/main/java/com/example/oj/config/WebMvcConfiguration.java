package com.example.oj.config;

//import com.example.oj.interceptor.JwtTokenAdminInterceptor;

import com.example.oj.interceptor.JwtTokenAdminInterceptor;
import com.example.oj.interceptor.LoggingFilter;
import com.example.oj.mapper.JacksonObjectMapper;
import com.example.oj.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
@EnableTransactionManagement
@EnableSpringDataWebSupport
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

	@Autowired
	private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

	@Autowired
	UserRepository userRepository;

	//	/**
	//	 * 注册自定义拦截器
	//	 *
	//	 * @param registry
	//	 */
	//	protected void addInterceptors(InterceptorRegistry registry) {
	//		log.info("开始注册自定义拦截器...");
	//		registry.addInterceptor(jwtTokenAdminInterceptor)
	////                .addPathPatterns("/**")
	////                .excludePathPatterns("/**")
	//				.excludePathPatterns("/register")
	//				.excludePathPatterns("/login");
	//	}
	@Bean
	public UserDetailsService userDetailsService() {
		return username -> userRepository.findByUsername(username)
		//                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
		;
	}

	@Override
	protected void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
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
	//	/**
	//	 * 设置静态资源映射
	//	 * @param registry
	//	 */
	//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
	//        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
	//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	//    }
	//
	//    @Override
	//    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	//        log.info("拓展消息转换器");
	//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	//        converter.setObjectMapper(new JacksonObjectMapper());
	//        converters.add(0, converter);
	//    }
}
