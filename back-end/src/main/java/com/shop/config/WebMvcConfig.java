package com.shop.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {

    private final long MAX_AGE_SECS=3600;  //1시간


    @Value("${uploadPath}")
    String uploadPath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .setCachePeriod(60*10*6)  // 1시간
                .setCachePeriod(60*10*6)
                .addResourceLocations(uploadPath);

        registry
                .addResourceHandler("/upload/**") //jsp 페이지에서 /upload/**  이런주소 패턴이 나오면 발동
                .addResourceLocations(uploadPath+"/upload/")
                .setCachePeriod(60*10*6)  // 1시간
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }




    @Override
    public void addCorsMappings(CorsRegistry registry) {

        /**  ★★★★★ nginx 연동의  경우 모든 경로에 대해 cors 허용 하면 안된다.
         *  또한,
         *  현재 프로젝트가 todo 와 api 인데
         *     restapi 가 아닌 경로까지 적용되면  restapi 아닌 페이지는 오규가 발생한다.
         /모든 경로에 대해 cors 허용 ex)
         registry.addMapping("/**")
         *      registry.addMapping("/cart/**"),
         *      registry.addMapping("/members/**")
         */

        //★★★★★  nginx 연동의  경우  루트  /** 으로 설정이 안된다 다음과 같이 개별 설정 ★★★★★
        registry.addMapping("/todo/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // 'Access-Control-Allow-Credentials' header 는 요청 시 자격 증명이 필요함
                .maxAge(MAX_AGE_SECS)
                .allowedOrigins(
                        "http://localhost:3000/"
                        ,"https://ma7front.p-e.kr/"
                ).exposedHeaders("authorization");  //authorization 헤더를 넘기 위해 exposedHeaders 조건을 추가


        registry.addMapping("/auth/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // 'Access-Control-Allow-Credentials' header 는 요청 시 자격 증명이 필요함
                .maxAge(MAX_AGE_SECS)
                .allowedOrigins(
                        "http://localhost:3000/"
                        ,"https://ma7front.p-e.kr/"
                ).exposedHeaders("authorization");  //authorization 헤더를 넘기 위해 exposedHeaders 조건을 추가


        registry.addMapping("/oauth2/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // 'Access-Control-Allow-Credentials' header 는 요청 시 자격 증명이 필요함
                .maxAge(MAX_AGE_SECS)
                .allowedOrigins(
                        "http://localhost:3000/"
                        ,"https://ma7front.p-e.kr/"
                ).exposedHeaders("authorization");  //authorization 헤더를 넘기 위해 exposedHeaders 조건을 추가


        registry.addMapping("/api/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS").allowedHeaders("*")
                .allowCredentials(true) // 'Access-Control-Allow-Credentials' header 는 요청 시 자격 증명이 필요함
                .maxAge(MAX_AGE_SECS)
                .allowedOrigins(
                        "http://localhost:3000/"
                        ,"https://ma7front.p-e.kr/"
                ).exposedHeaders("authorization"); //authorization 헤더를 넘기 위해 exposedHeaders 조건을 추가

    }

}
