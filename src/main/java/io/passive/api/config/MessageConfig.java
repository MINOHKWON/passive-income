package io.passive.api.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class MessageConfig implements WebMvcConfigurer{
	/**
	 * 메시지 소스 경로 지정
	 * @MethodName : messageSource
	 * @return
	 * @returnCode :
	 */
	@Bean 
	public ReloadableResourceBundleMessageSource messageSource(){
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource(); 
		messageSource.setBasename("classpath:/messages/messages"); 
		messageSource.setDefaultEncoding("UTF-8"); 
		messageSource.setCacheSeconds(180);
		// 제공하지 않는 언어로 요청이 들어왔을 때 MessageSource에서 사용할 기본d 언어정보. 
//		Locale.setDefault(Locale.CHINA); 
		return messageSource;
	}
	
	/**
	 * 다국어 기본언어 설정
	 * 한국어	: 	Locale.KOREA
	 * 영어		:	Locale.ENGLISH
	 * 중국어	:	Locale.CHINA
	 * @MethodName : localeResolver
	 * AcceptHeaderLocaleResolver : http 통신 때 사용되는 Accept-Language 필드를 이용하여 언어&국가정보를 인식. (Spring 기본 LocaleResolver)
	 * CookieLocaleResolver : 쿠키를 이용해 언어&국가정보를 인식.
	 * SessionLocaleResolver : 세션을 이용해 언어&국가정보를 인식.
	 * FixedLocaleResolver : 모든 요청에 대해 특정한 언어&국가정보로 인식.
	 * @return
	 * @returnCode :
	 */
	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        cookieLocaleResolver.setCookieName("cookie_lang");
        return cookieLocaleResolver;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
	    localeChangeInterceptor.setParamName("lang");
	    return localeChangeInterceptor;
	}
	
	/**
	 * 인터셉터로 등록한다.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/**");
	}
}
