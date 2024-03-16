package com.shop.config.jwt;


import com.shop.config.auth.PrincipalDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenUtil {

    // 시크릿 키를 담는 변수
    private SecretKey cachedSecretKey;

    /**
     $ echo '0-p7n#6s4l$ncdoui7+(^q(7^b^tt4@^i@6-q516f=aw-9%@fsdkj52423ksd9fs905235K$!$#49'|base64
     => git bash 에서 base64 로 인코딩한 값
     private static final String SECRET_KEY = "MC1wN24jNnM0bCRuY2RvdWk3KyhecSg3XmJedHQ0QF5pQDYtcTUxNmY9YXctOSVAZnNka2o1MjQyM2tzZDlmczkwNTIzNUskISQjNDkK";
     */
    private  final String SECRET_KEY= "MC1wN24jNnM0bCRuY2RvdWk3KyhecSg3XmJedHQ0QF5pQDYtcTUxNmY9YXctOSVAZnNka2o1MjQyM2tzZDlmczkwNTIzNUskISQjNDkK";



    //1.인증 키생성
    public SecretKey getSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
        SecretKey secretKey1 = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());

        if (cachedSecretKey == null) cachedSecretKey = secretKey1;
        return cachedSecretKey;
    }

    //2.토큰 정보 파싱
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    //3.토큰을 이용하여 유저아이디값 가져오기  반환값 id
    public String getUsername(String token) {
        return extractAllClaims(token).get("memberId", String.class);
    }


    /**
     * 4. 토큰 생성
     * @param memberId
     * @return
     */
    private String doGenerateToken(Long memberId, long expireTime) {
        Claims claim = Jwts.claims();
        claim.put("memberId", memberId);

        //기한 지금으로부터 1일로 설정
        // Date expiryDate =Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        //Date now = new Date();  now.getTime() 으로 하면 오류
        Date expireDate = new Date(System.currentTimeMillis() + expireTime);

                //JWT Token 생성
        return Jwts.builder()
                .setClaims(claim)
                .signWith(getSecretKey())  //토큰 서명 설정
                .setSubject(String.valueOf(memberId))
                .setIssuer("macaronics app")
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .compact(); //문자열로 압축
    }

    //5.접근 토큰 생성
    public String generateAccessToken(Long memberId, long expireTime) {
        return doGenerateToken(memberId, expireTime);
    }


    //6.갱신 토큰
    public String generateRefreshToken(Long memberId, long expireTime) {
        return doGenerateToken(memberId, expireTime);
    }


    //7.토큰 만료 여부
    public Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }


    //8.토큰 유효성 체크
    public Boolean validateToken(String token, PrincipalDetails userDetails) {
        String memberId = getUsername(token);
        return memberId.equals(String.valueOf(userDetails.getId())) && !isTokenExpired(token);
    }


    //9.토큰 남은시간
    public long getRemainMilliSeconds(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        Date now = new Date();
        return expiration.getTime() - now.getTime();
    }


    /**
     * 토큰 확인
     * parseClaimsJws 메시드 Base 64로 디코딩 및 파싱.
     * 즉, 헤더와 페이로드를 setSigningKey 로 넘어온 시크릿을 이용해 서명 후, token 의 서명과 비교.
     * 위조되지 않았다면 페이로드(Claims) 리턴, 위조라면 예외를 날림 , 그중 우리는 memberId 가 필요하므로 getBody 를 부른다.
     * @param token
     * @return
     */
    public String validateAndGetUserId(String token){
        Claims claims=Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }



}
