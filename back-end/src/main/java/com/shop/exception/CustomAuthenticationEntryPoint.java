package com.shop.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.config.filter.JwtAuthenticationFilter;
import com.shop.dto.api.todo.ResDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 토큰 필터 에러 처리
 */
@Component
@Log4j2
public class CustomAuthenticationEntryPoint extends RuntimeException implements AuthenticationEntryPoint {

    /**
     * 1.API  에러 설정
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        log.info("=========  JwtAuthenticationEntryPoint getMessage  : {}", authException.getMessage());
        log.info("=========  JwtAuthenticationEntryPoint getMessage  : {}", request.getContentType());

        //예외 페이지가 아니고, json 타입이 아닌 경우  세션 페이지로 에러 페이지 이동처리
        if (JwtAuthenticationFilter.exclusionPages(request) &&
                StringUtils.hasText(request.getContentType()) &&
                !request.getContentType().equals("application/json")) {
            response.sendRedirect(sessionErrorPage(authException.getMessage(), response));
            return;
        }


        String errorCode =(String) request.getAttribute("errorCode");
        String message =(String) request.getAttribute("message");

        log.info("******* JwtAuthenticationEntryPoint  토큰 필터 에러 처리 : errorCode :{} , message :{}  ",errorCode, message);
        if(StringUtils.hasText(errorCode) ||
                ( StringUtils.hasText(authException.getMessage()) &&
                org.thymeleaf.util.StringUtils.contains(authException.getMessage(),"authentication") )){

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            ResDTO<Object> errorRes ;
            if(org.thymeleaf.util.StringUtils.contains(authException.getMessage(),"authentication")){
                errorRes=ResDTO.builder().code(-1).message("권한이 없습니다. 해당 리소스에 접근할 수 없습니다.").errorCode("ACCESS_DENIED").build();
                //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            }else{
                errorRes=ResDTO.builder().code(-1).message(message).errorCode(errorCode).build();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(errorRes);
            response.getWriter().print(result);
        }

    }



    /**
     * 2.세션 페이지 에러 페이지 설정
     * @param message
     * @param response
     * @return
     */
    private String sessionErrorPage(String message, HttpServletResponse response){
        String erroPage="/error/404";
        if(StringUtils.hasText(message)){

            if(org.thymeleaf.util.StringUtils.contains(message,"authentication")){
                erroPage="/error/404";
            }else{
                erroPage="/error/500";
            }
        }
        return erroPage;
    }

    
}