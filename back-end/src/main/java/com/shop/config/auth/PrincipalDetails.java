package com.shop.config.auth;

import com.shop.constant.Role;
import com.shop.entity.Member;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

//시큐리티가 login 주소 요청이 오면 낚아채서 로그인을 진행시킨다
//로그인을 진행이 완료가 되면 시큐리티  session 을 만들어 줍니다. (security ContectHolder) 세션 정보 저장
//오브젝트 => Authentication 타입 객체
//Authentication 안에 User 정보가 있어야 함.
//User 오브젝트타입 => UserDetails 타입 객체

//Security Session => Authentication   => UserDetails
@Getter
@Log4j2
public class PrincipalDetails implements UserDetails, OAuth2User {


    @Serial
    private static final long serialVersionUID = 1L;
    private final Member member; // 콤포지션
    private final long id;
    private final String idStr; //아이디를 문자열로 반환
    private final String email;
    private final String username;


    /** 다음은  OAuth2User 를 위한 필드 */
    private String authProviderId;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;


    public PrincipalDetails(Member member, Map<String, Object> attributes){
        this.authProviderId=member.getAuthProviderId();
        this.attributes=attributes;
        this.authorities= Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        this.id=member.getId();
        this.idStr=member.getId().toString();
        this.username=member.getUsername();
        this.email = member.getEmail();
        this.member = member;
    }



    // 일반로그인
    public PrincipalDetails(Member member) {
        this.id=member.getId();
        this.idStr=member.getId().toString();
        this.username=member.getUsername();
        this.email = member.getEmail();
        this.member = member;
        this.authorities=getAuthorities();
    }


    /**
     * 사용자에게 부여된 권한을 반환합니다. null을 반환할 수 없습니다.
     */
    // 해당 User 의 권한을 리턴하는 곳!!
    //권한:한개가 아닐 수 있음.(3개 이상의 권한)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collector=new ArrayList<>();
        log.info("********* 시큐리티 로그인 :" +member.getRole().toString());
        collector.add(()-> member.getRole().toString());
        return collector;
    }






    /**
     * 사용자를 인증하는 데 사용된 암호를 반환합니다.
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 사용자를 인증하는 데 사용된 사용자 이름을 반환합니다. null을 반환할 수 없습니다.
     */
    @Override
    public String getUsername() {
        return member.getUsername();
    }

    /**
     * 사용자의 계정이 만료되었는지 여부를 나타냅니다. 만료된 계정은 인증할 수 없습니다.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 사용자가 잠겨 있는지 또는 잠금 해제되어 있는지 나타냅니다. 잠긴 사용자는 인증할 수 없습니다.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 사용자의 자격 증명(암호)이 만료되었는지 여부를 나타냅니다. 만료된 자격 증명은 인증을 방지합니다.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자가 활성화되었는지 비활성화되었는지 여부를 나타냅니다. 비활성화된 사용자는 인증할 수 없습니다.
     */
    @Override
    public boolean isEnabled() {
        // 우리 사이트 1년동안 회원이 로그인을 안하면!! 휴먼 계정으로 하기로 함.
        // 현재시간-로긴시간=>1년을 초과하면 return false;
        return true;
    }

    public boolean isWriteEnabled() {
        if (member.getRole().equals(Role.USER))
            return false;
        else
            return true;
    }


    public boolean isWriteAdminAndManagerEnabled() {
        if (member.getRole().equals(Role.ADMIN )|| member.getRole().equals(Role.USER))
            return true;
        else
            return false;
    }


    /**
     * 다음 OAuth2User 를 위한 메소드
     * @return
     */
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.authProviderId; //name 대신 id를 리턴한다.
    }




}