package com.shop.config.auth;


import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class PrincipalDetailsService  implements UserDetailsService {


    private final MemberRepository memberRepository;

    /*
        1.패스워드는 알아서 체킹하니깐 신경쓸 필요 없다
        2.리턴이 잘되면 자동으로 User 세션을 만든다.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if(member==null) throw new UsernameNotFoundException(email);

        return new PrincipalDetails(member);
    }




    //API 및 Oauth2 로그인시 memberId 로
    public UserDetails loadUserApiByUsername(long memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(memberId).orElse(null);
        if(member==null)  throw new UsernameNotFoundException(String.valueOf(memberId));

        return new PrincipalDetails(member);
    }



}



