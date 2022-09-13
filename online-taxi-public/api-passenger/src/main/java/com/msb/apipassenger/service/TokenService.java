package com.msb.apipassenger.service;

import com.msb.internalcommon.constant.CommonStatusEnum;
import com.msb.internalcommon.constant.TokenConstant;
import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.dto.TokenResult;
import com.msb.internalcommon.response.TokenResponse;
import com.msb.internalcommon.util.JwtUtils;
import com.msb.internalcommon.util.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    public ResponseResult refreshToken(String refreshTokenSrc){
        //解析refreshtoken
        TokenResult tokenResult = JwtUtils.checkToken(refreshTokenSrc);

        if(null==tokenResult){
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(),CommonStatusEnum.TOKEN_ERROR.getValue());
        }
        String phone = tokenResult.getPhone();
        String identity = tokenResult.getIdentity();

        //读取redis中的token
        String refreshTokenKey = RedisPrefixUtils.generatorTokenKey(phone, identity, TokenConstant.REFRESH_TOKEN);
        String refreshTokenRedis = stringRedisTemplate.opsForValue().get(refreshTokenKey);
        //校验token
        if(StringUtils.isBlank(refreshTokenRedis)||!(refreshTokenSrc.trim().equals(refreshTokenRedis.trim()))){
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(),CommonStatusEnum.TOKEN_ERROR.getValue());
        }

        //生成双token
        String accessToken = JwtUtils.generatorToken(phone, identity, TokenConstant.ACCESS_TOKEN);
        String refreshToken = JwtUtils.generatorToken(phone, identity, TokenConstant.REFRESH_TOKEN);

        String accessTokenKey = RedisPrefixUtils.generatorTokenKey(phone, identity, TokenConstant.ACCESS_TOKEN);

        //放入redis

        stringRedisTemplate.opsForValue().set(accessTokenKey,accessToken,30,TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(refreshTokenKey,refreshToken,31, TimeUnit.DAYS);

//        stringRedisTemplate.opsForValue().set(accessTokenKey,accessToken,10,TimeUnit.SECONDS);
//        stringRedisTemplate.opsForValue().set(refreshTokenKey,refershToken,50, TimeUnit.SECONDS);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);
    }


}
