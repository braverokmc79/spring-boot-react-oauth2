package com.shop.config.audit;

import com.shop.config.auth.PrincipalDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = "";
        if(authentication != null){
            //userId = authentication.getName();
            //아이디 및 이메일로 할경우  oauth2 로 인증처리 된경우 이메일이 없는 경우가 있어 오류남 따라서, primarykey Id 설정
            try{
                PrincipalDetails principal= (PrincipalDetails) authentication.getPrincipal();

                userId = principal.getIdStr();
            }catch (Exception e){
                userId = "anonymous";
            }

        }
        return Optional.of(userId);
    }

}