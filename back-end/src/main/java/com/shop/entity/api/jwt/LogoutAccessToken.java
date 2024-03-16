package com.shop.entity.api.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;

@Getter
@RedisHash("logoutAccessToken")
@AllArgsConstructor
@Builder
public class LogoutAccessToken {

    @Id
    private Long id;

    private String refreshToken;

    
    //기본값 -1로  Redis 에 영구적으로 유지, 로그아웃 시간 Milliseconds 형식
    @TimeToLive
    private Long logoutTimeMilliseconds;

    //로그아웃 시간 yyyy-MM-dd HH:mm:ss(EEE) 형식
    private String logoutTime;

    public static LogoutAccessToken of(Long memberId, String refreshToken, Long logoutTimeMilliseconds, String logoutTime) {
        return LogoutAccessToken.builder()
                .id(memberId)
                .refreshToken(refreshToken)
                .logoutTimeMilliseconds(logoutTimeMilliseconds)
                .logoutTime(logoutTime)
                .build();
    }
}
