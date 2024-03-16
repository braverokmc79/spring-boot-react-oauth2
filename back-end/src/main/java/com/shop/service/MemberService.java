package com.shop.service;

import com.shop.dto.MemberDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static groovyjarjarantlr4.v4.gui.Trees.save;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }


    private void validateDuplicateMember(Member member){
        if(member ==null || member.getUsername() == null){
            throw new RuntimeException("Invalid arguments");
        }

        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember!=null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

        final String username = member.getUsername();
        if(memberRepository.existsByUsername(username)){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }


    /**
     * API 회원 등록
     * @param member
     * @return
     */
    public Member createMember(final Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }


    /**
     * API 로그인
     * @param username
     * @param password
     * @return
     */
    public Member getByCredentials(final String username, final String password){
        return memberRepository.findByUsernameAndPassword(username, password);
    }

    public Member getMemberUsername(final String username){
        return memberRepository.findByUsername(username);
    }



}
