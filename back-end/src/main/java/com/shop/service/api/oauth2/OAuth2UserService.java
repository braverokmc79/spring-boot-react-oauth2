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
        Member member=null;

        //1.OAUTH2_ID 를 통해 , DB 회원 테이블에 OAUTH2 로 가입된 회원이 존재하면 해당 정보를 가져온다.(바로 로그인 처리)
        if(memberRepository.existsByAuthProviderId(OAUTH2_ID)){
            log.info("======>1.기존에 가입된 OAUTH2 회원  OAUTH2_ID 확인후 바로 로그인 처리");
            member = memberRepository.findMemberByAuthProviderId(OAUTH2_ID);
        }else{
            //2.신규가입처리 - 1) DB 회원 테이블에 OAUTH2 로 가입된 정보가 없다. 따라서 신규가입처리 진행하는데, 이메일이 있을 경우를 다음과 같이 확인 가입처리 한다.
            //OAUTH2 에서 가져온 email 정보가 존재 한다. 해당 이메일이 DB 에 존재하는지 확인 ,
            //만약에 존재 할경우 해당 정보를 가져와서 AuthProvider , AuthProviderId  컬럼값만 업데이트 처리한다.

            if(StringUtils.hasText(email)){
                // DB 에서 해당 이메일로 회원정보를 가져온다.
                Member memberEntity = memberRepository.findByEmail(email);
                if(memberEntity!=null){
                    //1)널이 아니면 DB 데이터에 업데이트 처리
                    log.info("======>2-1.OAUTH 이메일 로 업데이트 후 회원가입 처리");
                    memberEntity.setAuthProvider(authProvider);
                    memberEntity.setAuthProviderId(OAUTH2_ID);
                    member=memberEntity;
                }
            }
        }

        if(member==null){
            //2.신규가입처리 - 2)  그냥 전부 새롭게 신규 등록
            log.info("======>2-2.OAUTH  새롭게 신규 등록");
            member=createOauth2Member( authProvider , username,  OAUTH2_ID,   email);
        }


        PrincipalDetails principalDetails = new PrincipalDetails(member, oAuth2User.getAttributes());
        log.info("========================2 Oauth2User  토큰 반환시 정보값 ================ {}", principalDetails.getMember().getId());
        return principalDetails;
    }


    //oauth2 DB 등록
    private Member createOauth2Member(String authProvider ,String username, String OAUTH2_ID, String  email){
        MemberDto memberDto = MemberDto.builder()
                .username(authProvider+"_"+username)
                .email(email)
                .authProvider(authProvider)
                .authProviderId(OAUTH2_ID)
                .role(Role.USER)
                .build();
        Member member = MemberDto.oauth2CreateMember(memberDto);
        return memberRepository.save(member);
    }



    
}
