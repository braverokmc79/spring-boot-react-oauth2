package com.shop.service.api.jwt;

import com.shop.config.auth.PrincipalDetails;
import com.shop.exception.CustomAuthenticationException;
import com.shop.config.jwt.JwtTokenUtil;
import com.shop.dto.MemberDto;
import com.shop.dto.api.jwt.TokenDto;
import com.shop.entity.Member;
import com.shop.entity.api.jwt.LogoutAccessToken;
import com.shop.entity.api.jwt.RefreshToken;
import com.shop.repository.MemberRepository;
import com.shop.repository.api.jwt.LogoutAccessTokenRedisRepository;
import com.shop.repository.api.jwt.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * 추가된 라이브러리를 사용해서 JWT를 생성하고 검증하는 컴포넌트
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class JwtTokenProviderService {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final MemberRepository memberRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final JwtTokenUtil jwtTokenUtil;


    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;


    /**
     * 1. 로그인시 접근 토큰 생성
     * @param member
     * @return
     */
    public TokenDto create(Member member){
        //1.접근 토큰 생성
        String accessToken = jwtTokenUtil.generateAccessToken(member.getId(), accessExpirationTime);
        //2.갱신토큰 생성후 redis 에 저장
        RefreshToken refreshToken = saveRefreshToken(member.getId());
        log.info("*********** 접근토큰 : {}" ,accessToken);
        log.info("*********** 갱신토큰 : {}",refreshToken);
        return TokenDto.of(accessToken, refreshToken.getRefreshToken());
    }

    // Oauth2 를 위한 토큰 생성
    public TokenDto create(final Authentication authentication){
        PrincipalDetails principal = (PrincipalDetails)authentication.getPrincipal();
        return create(principal.getMember());

    }


    //갱신토큰을 redis 에 저장
    private RefreshToken saveRefreshToken(Long memberId) {
        RefreshToken refreshToken = RefreshToken.createRefreshToken(memberId, jwtTokenUtil.generateRefreshToken(memberId, refreshExpirationTime), refreshExpirationTime);
        refreshTokenRedisRepository.save(refreshToken);
        return refreshToken;
    }




    /**

     * 2. access token + refresh token 재발급 처리
     * @param memberId
     * @return
     */
    private TokenDto reissueRefreshToken( long memberId) {
        //access token + refresh token 재발급
        String accessToken = jwtTokenUtil.generateAccessToken(memberId, accessExpirationTime);
        RefreshToken refreshToken = saveRefreshToken(memberId);
        return TokenDto.of(accessToken, refreshToken.getRefreshToken());
    }





    /**
     * 3.토큰 재발행
     *
     *
     * ★
     * 현재 이 코드는 , 회원아이디 값을 키값을 설정 했기 때문에, 하나의 브라우저에서만 로그인 된다.
     * 정확이 말하면, 기존에 접속한 브라우저에서는   accessExpirationTime 만료시까지 유지 된다.
     * 즉, 새로운 브라우저로 로그인하면서  redis 에서  새로운 refresh token 값을 저장했기 때문이다.
     *
     *  ★여러 브라우저에서 가능하도록 하려면, refresh token 을 키값을 설정하면 된다.
     *
     *
     * @return
     */
    public MemberDto reissue(String refreshToken ) throws  Exception{

        //1.refreshToken 를 파싱해서 memberId 값을 가져온다.
        String memberId=validateAndGetUserId(refreshToken);
        log.info("1.refreshToken 를 파싱해서 memberId 값을 가져온다.  {}",memberId);

        //2.Redis 저장된 토큰 정보를 가져온다.
        RefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        log.info("2.Redis 저장된 토큰 정보를 가져온다. {}", redisRefreshToken.getRefreshToken());

        //3.redis 에 저장된 토큰과 값과 파라미터의 갱신토크와 비교해서 같으면 갱신토큰 발급처리
        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            TokenDto tokenDto = reissueRefreshToken( Long.parseLong(memberId));
            Member member = memberRepository.findById(Long.parseLong(memberId)).orElse(null);
            if(member!=null){
                MemberDto memberDto = MemberDto.of(member);
                memberDto.setToken(tokenDto);
                return memberDto;
            }throw  new CustomAuthenticationException("해당하는 유저가 없습니다.", "USER_NOT_FOUND");
        }
        throw new CustomAuthenticationException("유효하지 않은 토큰 입니다.","INVALID_TOKEN" );
    }




    /**
     * 토큰 확인
     * @param token
     * @return
     */
    public String validateAndGetUserId(String token) throws  Exception{
        return jwtTokenUtil.validateAndGetUserId(token);
    }


    public void logout(String refreshToken) throws  Exception{
        //1.refreshToken 를 파싱해서 memberId 값을 가져온다.
        String memberId=validateAndGetUserId(refreshToken);

        //2.redis 에서 삭제 처리
        refreshTokenRedisRepository.deleteById(memberId);

        String logOutTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss(EEE)",  Locale.ENGLISH));

        //3.redis 에 로그아웃 날짜 저장
        logoutAccessTokenRedisRepository.save(LogoutAccessToken.of(Long.valueOf(memberId), refreshToken, System.currentTimeMillis(), logOutTime));
    }



}
