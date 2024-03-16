package com.shop.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.config.filter.JwtAuthenticationFilter;
import com.shop.config.oauth2.OAuth2SuccessHandler;
import com.shop.exception.CustomAuthenticationEntryPoint;
import com.shop.service.api.oauth2.OAuth2UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@EnableGlobalMethodSecurity(prePostEnabled = true)//spring boot 3 이상 부터 기본값 true 업데이트 됩
public class SecurityConfig  {

    private  final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final OAuth2UserService oAuth2UserService;

    //OAuth2 로그인 성공시 토큰 생성
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public JPAQueryFactory queryFactory(EntityManager em){
        return new JPAQueryFactory(em);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //JWT 를 사용하면  csrf 보안이 설정 필요없다. 그러나 여기 프로젝트에서는 세션방식과 jwt 방식을 둘다적용 중이라 특정 페이지만 제외 처리
        http.csrf(c -> {
            c.ignoringRequestMatchers("/admin/**","/api/**", "/oauth2/**" ,"/error/**");
        });

        //1.csrf 사용하지 않을 경우 다음과 같이 설정
        //http.csrf(AbstractHttpConfigurer::disable);

        //2. h2 접근 및 iframe 접근을 위한 SameOrigin (프레임 허용) & 보안 강화 설정 할경우
        //http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        //3.HTTP 기본 인증 만 할경우 다음과 같이 처리
        //httpSecurity.formLogin(AbstractHttpConfigurer::disable);

        //3.jwt token 만 인증 처리 할경우  basic 인증을 disable 한다. 그러나 이 프로젝트는 세션+jwt 이라 disable 설정은 하지 않는다.
        //httpSecurity.httpBasic(AbstractHttpConfigurer::disable);

        //4.JWT 만 사용할경우 세션 관리 상태 없음 설정 그러나, 이 프로젝트는  세션 + JWT 를 사용하지 때문에 주석
        //http.sessionManagement(sessionManagementConfigurer ->sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //세션방식의  form 로그인 처리
        http.formLogin(login-> login
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/", true)
                        .usernameParameter("email")
                        .failureUrl("/members/login/error"))
                .logout(logoutConfig ->logoutConfig.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")).logoutSuccessUrl("/"))
                .exceptionHandling(exceptionConfig-> exceptionConfig.authenticationEntryPoint(new Http403ForbiddenEntryPoint()));


        http.authorizeHttpRequests(request->request
                .requestMatchers("/css/**", "/js/**", "/img/**","/images/**").permitAll()
                .requestMatchers("/", "/members/**", "/item/**", "/main/**", "/error/**" ).permitAll()

                 //JWT 일반 접속 설정
                .requestMatchers("/auth/**",  "/oauth2/**").permitAll()
                .requestMatchers("/api/todo/**" ,"/api/auth/**", "/api/oauth2/**" ).permitAll()

                //JWT 관리자 페이지 설정
                .requestMatchers( "/api/admin/**").hasAuthority("ADMIN")


                //세션방식  --> 관리자 페이지는 설정
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
        );

         //api 페이지만  JWT  필터 설정(jwtAuthenticationFilter 에서 shouldNotFilter 메서드로 세션 페이지는 필터를 제외 시켰다.)
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        //에외 처리
        .exceptionHandling(exceptionConfig->exceptionConfig.authenticationEntryPoint(new CustomAuthenticationEntryPoint()) );

        //oauth2Login 로그인 처리
        http.oauth2Login(oauth2Configurer -> oauth2Configurer
                //1) oauth2 로그인 페이지  -> oauth2 github 에서 인증후 로그인되면  callback 호출
                //.loginPage("/oauth2/login") 

                //2)oauth2 callback 로  호출
                .redirectionEndpoint(rE ->rE.baseUri("/oauth2/callback/*"))
                //3)oAuth2UserService 에서 회원이 존재 하지 않으면 가입처리되고, 존재하면 해당 아이디를 호출하여 정보를 가져온다
                 .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuth2UserService))
                //4)성공시 oAuth2SuccessHandler 호출 되며 토큰 생성및 쿠키에 저장 시킨다.
               .successHandler(oAuth2SuccessHandler)
                //5)OAuth2.0 흐름 시작을 위한 엔드 포인트  임의 주소 /api/auth/authorize 추가하면
                //http://localhost:8080/oauth2/authorization/github url 주소와 동일하게
                //http://localhost:8080/api/auth/authorize/github  url 주소를 입력시 OAuth2 처리 페이지로 이동 처리된다.
               .authorizationEndpoint(anf -> anf.baseUri("/api/auth/authorize"))
        )
        //인증 실패시 Oauth2 흐름으로 넘어가는 것을 막고 응답코드 403을 반환처리
        .exceptionHandling(exceptionConfig->exceptionConfig.authenticationEntryPoint(new Http403ForbiddenEntryPoint()));

       return http.build();
    }





}
