package com.shop.service.api.oauth2;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.config.auth.PrincipalDetails;
import com.shop.constant.Role;
import com.shop.dto.MemberDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("★★★★★★★★★★ OAuth2UserService loadUse");

        //1.DefaultOauth2UserService 의 기존 loadUser 를 호출한다. 이 메서드가 user-info-uri 를 이용해 사용자 정보를 가져오는 부분이다.
        final OAuth2User oAuth2User =super.loadUser(userRequest);

        try{
            //2.디버깅을 돕기 위해 OAuth2User 사용자 정보를 출력
            log.info("******** OAuth2User attributes : {}", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes())) ;
        }catch (Exception e){
            e.printStackTrace();
        }

        log.info("******** OAuth2UserService loadUser : {} , getAttributes() : {}", oAuth2User , oAuth2User.getAttributes());

        //3.login 필드를 가져온다.
        final String username = oAuth2User.getAttributes().get("login").toString();
        final String authProviderId = oAuth2User.getAttributes().get("id").toString();
        String email =null;
        if(oAuth2User.getAttributes().get("email")!=null){
            email = oAuth2User.getAttributes().get("email").toString();
        }
        final String authProvider=userRequest.getClientRegistration().getClientName();


        //OAUTH2_ID = Oauth2(naver,github,google)_authProviderId    ex) Github_1234
        String OAUTH2_ID= authProvider+"_"+authProviderId;


        /**
         각 소셜 로그인 제공자가 반환하는 유저 정보, 즉 attributes 에 들어 있는 내용은 제공자 마다 각각 다르다.
         email을 아이디로 사용하는 제공자는 email 필드가 있을 것이고, 깃허브의 경우에는 login 필드가 있다.
         따라서 여러 소셜 로그인과 통홥하려면 이 부분을 알맞게 파싱해야 한다.
         *
         */

        //authProvider :GitHub

        if(StringUtils.hasText(email)){
            //1.기존에 동일한 이메일 있을 경우 authProvider, 와 authProviderId 값만 업데이트
            Member memberEntity = memberRepository.findByEmail(email);
            memberEntity.setAuthProvider(authProvider);
            memberEntity.setAuthProviderId(OAUTH2_ID);

        }else if(!memberRepository.existsByAuthProviderId(OAUTH2_ID)){
            //2.유저가 존재하지 않으면 새로 생성한다.
            MemberDto memberDto = MemberDto.builder()
                    .username(authProvider+"_"+username)
                    .email(email)
                    .authProvider(authProvider)
                    .authProviderId(OAUTH2_ID)
                    .role(Role.USER)
                    .build();

            Member member = MemberDto.oauth2CreateMember(memberDto);
            memberRepository.save(member);
        }

        //3.authProvider 통해 회원 정보를 불러온다.
        Member member=memberRepository.findMemberByAuthProviderId(OAUTH2_ID);


        PrincipalDetails principalDetails = new PrincipalDetails(member, oAuth2User.getAttributes());
        log.info("========================2 Oauth2User  토큰 반환시 정보값 ================ {}", principalDetails.getMember().getId());
        return principalDetails;

    }


}
