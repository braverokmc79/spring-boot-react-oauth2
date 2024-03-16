package com.shop.config.oauth2;

import com.shop.dto.api.jwt.TokenDto;
import com.shop.service.api.jwt.JwtTokenProviderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인 성공시 토큰 생성
 *
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProviderService jwtTokenProviderService;

    @Value("${serverRedirectFrontUrl}")
    private  String serverRedirectFrontUrl ;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        TokenDto tokenDto= jwtTokenProviderService.create(authentication);
        String redUrlPath=serverRedirectFrontUrl+"/sociallogin?accessToken=" +tokenDto.getAccessToken()
                +"&refreshToken="+tokenDto.getRefreshToken();
        response.sendRedirect(redUrlPath);
    }





    // private static final String LOCAL_REDIRECT_URL = "http://localhost:3000";

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//
//        TokenDto tokenDto= jwtTokenProviderService.create(authentication);
//        // response.getWriter().write(tokenDto.toString());
//        String redUrlPath="/sociallogin?accessToken=" +tokenDto.getAccessToken() +"&refreshToken="+tokenDto.getRefreshToken();
//        //response.sendRedirect("http://localhost:3000"+redUrlPath);
//
//        //RedirectUrlCookieFilter 에서 리다렉트시에 쿠키에 저장한 redirectUrl 값 가져오기
//        Optional<Cookie> optionalCookie =
//                Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(REDIRECT_URI_PARAM)).findFirst();
//        Optional<String> redirectUrl = optionalCookie.map(Cookie::getValue);
//
//        log.info(" 쿠키값 redirectUrl   저장 : {} ", redirectUrl);
//        response.sendRedirect(  redirectUrl.orElseGet(()->LOCAL_REDIRECT_URL)+redUrlPath);
//    }



}
