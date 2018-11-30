package org.openpaas.ieda;

import java.util.Locale;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.api.LocalDirectoryConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAsync
@ComponentScan
@Configuration
@EnableAutoConfiguration
public class OpenpaasIedaControllerApplication implements CommandLineRunner {

public static void main(String[] args) {
    //LocalDirectoryConfiguration.initialize();
    //spring boot 어플리케이션 구동
    SpringApplication app = new SpringApplication(OpenpaasIedaControllerApplication.class);
    app.addListeners(new ApplicationPidFileWriter("app.pid"));
    app.run(args);
}

    @Override
    public void run(String... args) throws Exception {
    	LocalDirectoryConfiguration.initialize();
    }

    @Bean
    public ModelMapper modelMapper() {
    //ModelMapper 빈 등록
    return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
    //ObjectMapper 빈 등록
    return new ObjectMapper();
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
