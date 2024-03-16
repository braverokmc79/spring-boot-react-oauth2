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




}
