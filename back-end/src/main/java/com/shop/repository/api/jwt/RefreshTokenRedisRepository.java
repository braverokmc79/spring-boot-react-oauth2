package com.shop.repository.api.jwt;


import com.shop.entity.api.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}


