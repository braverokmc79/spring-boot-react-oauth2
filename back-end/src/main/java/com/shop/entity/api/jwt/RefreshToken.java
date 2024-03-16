package com.shop.entity.api.jwt;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 timeToLive는 유효시간을 값으로 초 단위를 의미합니다.
  현재 14_400초 4시간으로 설정

 , timeToLive = 14440
 */

@Getter
@RedisHash(value = "refreshToken")
@AllArgsConstructor
@Builder
@ToString
@NoArgsConstructor
public class RefreshToken {

    //여기서 주의할 점은 @Id 어노테이션입니다.
    //java.persistence.id가 아닌 org.springframework.data.annotation.Id  를 import 해야 됩니다.
    //Refresh Token은 Redis에 저장하기 때문에 JPA 의존성이 필요하지 않습니다. (persistence로 하면 에러납니다.)
    // 또한  id 변수이름 그대로 사용해야지 CrudRepository 의 findById 를 사용할 수 있다.
    @Id
    private Long id;

    private String refreshToken;

    @TimeToLive  //기본값 무한
    private Long expiration;

    public static RefreshToken createRefreshToken(Long memberId, String refreshToken, Long remainingMilliSeconds) {
        return RefreshToken.builder()
                .refreshToken(refreshToken)
                .id(memberId)
                .expiration(remainingMilliSeconds / 1000)
                .build();
    }

}
