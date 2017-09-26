package org.openpaas.ieda;

import java.util.Locale;

import javax.servlet.MultipartConfigElement;

import org.openpaas.ieda.common.security.SecurityAuthenticationFailure;
import org.openpaas.ieda.common.security.SecurityAuthenticationLogout;
import org.openpaas.ieda.common.security.SecurityAuthenticationSuccess;
import org.openpaas.ieda.common.security.SecurityPath;
import org.openpaas.ieda.common.security.SecuritySessionListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAsync
@ComponentScan
@Configuration
@EnableAutoConfiguration
public class TestBeansConfiguration {
	
	@Bean
    public ObjectMapper objectMapper() {
    	//ObjectMapper 빈 등록
    	return new ObjectMapper();
    }
    
    @Bean
	SecurityAuthenticationLogout logoutSuccessHandler() {
        return new SecurityAuthenticationLogout();
    }

	@Bean
	SecurityAuthenticationSuccess successHander() {
        return new SecurityAuthenticationSuccess();
    }	
	
	@Bean
	SecurityAuthenticationFailure failureHandler() {
        return new SecurityAuthenticationFailure();
    }	
	
	@Bean
    SessionRegistry sessionRegistry() {            
        return new SessionRegistryImpl();
    }

	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
	    return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
	}
	
	@Bean
	public SecuritySessionListener securitySessionListener(){
		return new SecuritySessionListener();
	}
	
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		//File 사이즈 설정
	    MultipartConfigFactory factory = new MultipartConfigFactory();
	    factory.setMaxFileSize("5000000000");
	    factory.setMaxRequestSize("5000000000");

	    return factory.createMultipartConfig();
	}

	@Bean
	public MultipartResolver multipartResolver() {
	    return new StandardServletMultipartResolver();
	}
	
	@Bean
	SecurityPath securityPath() {
        return new SecurityPath();
    }
	
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        //WEB-INF 밑에 해당 폴더에서 properties를 찾는다.
        messageSource.setBasename("messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
 
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
        //request로 넘어오는 language parameter를 받아서 locale로 설정 한다.
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }
 
    @Bean(name = "localeResolver")
    public LocaleResolver sessionLocaleResolver(){
        //세션 기준으로 로케일을 설정 한다.
        SessionLocaleResolver localeResolver=new SessionLocaleResolver();
        //쿠키 기준(세션이 끊겨도 브라우져에 설정된 쿠키 기준으로)
//        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
 
        //최초 기본 로케일을 강제로 설정이 가능 하다.
        localeResolver.setDefaultLocale(new Locale("ko_KR"));
        return localeResolver;
    }
    public void addInterceptors(InterceptorRegistry registry) {
        //Interceptor를 추가 한다.
        registry.addInterceptor(localeChangeInterceptor());
    }

}
