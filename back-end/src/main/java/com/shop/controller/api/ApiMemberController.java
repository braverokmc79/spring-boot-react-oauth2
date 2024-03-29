package com.shop.controller.api;

import com.shop.dto.MemberDto;
import com.shop.dto.MemberFormDto;
import com.shop.dto.api.jwt.TokenDto;
import com.shop.dto.api.todo.ResDTO;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import com.shop.service.api.jwt.JwtTokenProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class ApiMemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProviderService tokenProvider;

    /**
     * API 회원 가입 처리
     * @param memberFormDto
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody MemberFormDto memberFormDto){
        try{
            if(memberFormDto==null || memberFormDto.getPassword()==null){
               throw new RuntimeException("비밀번호가 유효하지 않습니다.");
            }

            //1.요청을 이용해 저장할 유저 만들기
            memberFormDto.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));
            Member member = Member.createMember(memberFormDto);

            //2.서비스를 이용해 리포지터리에 유저 저장
            MemberDto memberDto = MemberDto.of(memberService.createMember(member));

            return ResponseEntity.ok(ResDTO.builder().code(1).message("success").data(memberDto).build());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ResDTO.builder().code(-1).errorCode(e.getMessage()).build());
        }
    }


    /**
     * 로그인
     * @param memberFormDto
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody MemberFormDto memberFormDto){
        Member memberEntity = memberService.getMemberUsername(memberFormDto.getUsername());
        if(memberEntity!=null && passwordEncoder.matches(memberFormDto.getPassword(), memberEntity.getPassword())){

           log.info("3. signIn================>{}"  ,memberFormDto.getUsername());
            //토큰 생성
            final TokenDto tokenDto = tokenProvider.create(memberEntity);
            MemberDto memberDto = MemberDto.of(memberEntity);
            memberDto.setToken(tokenDto);

           return ResponseEntity.ok(ResDTO.builder().code(1).message("success").data(memberDto).build());
        }else{
            return ResponseEntity.badRequest().body(ResDTO.builder().code(-1).message("아이디 또는 비밀번호가 일치하지 않습니다.").errorCode("not match").build());
        }
    }


    /**확인 사항
     * 1.갱신토큰값 과 재발행 토큰값과 비교시 갱신토큰값이 작으면 접근토큰 및 갱신토큰 발행
     * 2.
     * 갱신토큰 재발행
     * @param refreshToken
     * @return
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader(value = "RefreshToken") String refreshToken) {
        log.info("갱신 토큰 발행 =======================>");
        try{
            return ResponseEntity.ok(ResDTO.builder().code(1).message("success")
                    .data(tokenProvider.reissue(refreshToken)).build());
        }catch (Exception e){
            //갱신토큰이 유요하지 않을 경우 code 값을 -1로 주고, 프론트에서 "refreshToken is invalid" 메시지 확인후 로그아웃 처리한다.
            return ResponseEntity.badRequest().body(ResDTO.builder().code(-1).message("갱신 토큰이 유요하지 않습니다.").errorCode("INVALID_REFRESH_TOKEN").build());
        }
    }


    /**
     * 로그 아웃 처리
     * redis 에서 저장된  memberId  + refresh token  삭제 처리 한다.
     *
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "RefreshToken") String refreshToken)   {
        log.info("로그 아웃 처리 =======================>");
        try {
            tokenProvider.logout(refreshToken);
            return ResponseEntity.ok(ResDTO.builder().code(1).message("success").build());
        }catch (Exception e) {
            return ResponseEntity.ok(ResDTO.builder().code(-1).message("로그아웃 처리 오류").errorCode(e.getMessage()).build());
        }
    }



}



