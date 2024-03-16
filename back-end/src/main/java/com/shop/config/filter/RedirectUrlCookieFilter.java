package com.shop.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class RedirectUrlCookieFilter extends OncePerRequestFilter {

    public static final String REDIRECT_URI_PARAM = "redirect_url";
    private static final int MAX_AGE =180;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().startsWith("/api/auth/authorize")){
            try{
                log.info("*** RedirectUrlCookieFilter url {}", request.getRequestURI());
                //프론트엔드에서 전송한 리퀘스트 파라미터에서 redirect_url 을 가져온다.
                String redirectUrl = request.getParameter(REDIRECT_URI_PARAM);

                Cookie cookie = new Cookie(REDIRECT_URI_PARAM, redirectUrl);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(MAX_AGE);
            }catch(Exception ex){
                log.error("Could not set user authentication in security context  :", ex);
                log.info("unauthorized request");
            }
        }
        filterChain.doFilter(request, response);
    }

}
