package io.passive.api.config;

import io.passive.api.filter.HeaderFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	private static final String ALLOW_RESPONSE_HEADER = "Authorization";
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
		"classpath:/static/", "classpath:/public/",
		"classpath:/", "classpath:/resources/", "classpath:/META-INF/resources/",
		"classpath:/META-INF/resources/webjars/"
	};

	// CORS 설정
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").exposedHeaders(ALLOW_RESPONSE_HEADER).allowedOrigins("*").allowedMethods("*")
				.allowedHeaders("*").allowCredentials(false).maxAge(3600);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/login");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
	}

	@Bean
	public FilterRegistrationBean<HeaderFilter> getFilterRegistrationBean() {
		FilterRegistrationBean<HeaderFilter> registrationBean = new FilterRegistrationBean<>(createHeaderFilter());
		registrationBean.setOrder(Integer.MIN_VALUE);
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

	@Bean
	public HeaderFilter createHeaderFilter() {
		return new HeaderFilter();
	}
}
