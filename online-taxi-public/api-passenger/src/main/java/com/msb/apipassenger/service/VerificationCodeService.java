package com.msb.apipassenger.service;

import com.msb.apipassenger.remote.ServicePassengerUserClient;
import com.msb.apipassenger.remote.ServiceVerificationCodeClient;
import com.msb.internalcommon.constant.IdentityConstant;
import com.msb.internalcommon.constant.TokenConstant;
import com.msb.internalcommon.request.VerificationCodeDTO;
import com.msb.internalcommon.constant.CommonStatusEnum;
import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.response.NumberCodeResponse;
import com.msb.internalcommon.response.TokenResponse;

import com.msb.internalcommon.util.JwtUtils;
import com.msb.internalcommon.util.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private ServiceVerificationCodeClient serviceVerificationCodeClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    //验证码前缀
    private String verificationCodePrefix = "passenger-verification-code-";

    private String tokenPrefix = "token-";

    public ResponseResult generatorCode(String passengerPhone){
        //调用验证码服务，获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(5);

        int numberCode = numberCodeResponse.getData().getNumberCode();
        System.out.println(numberCode);
        //存入redis
        String key = RedisPrefixUtils.generatorKeyByPhone(passengerPhone);
        stringRedisTemplate.opsForValue().set(key,numberCode+"",2, TimeUnit.DAYS);

        //返回值
//        JSONObject result = new JSONObject();
//        result.put("code",1);
//        result.put("message","success");
//        ResponseResult<Object> result = new ResponseResult<>();
//        result.setCode(1).setMessage("success");

        //通过短信服务商把验证码发送到手机

        return ResponseResult.success();

    }

//    private String generatorKeyByPhone(String passengerPhone){
//        return verificationCodePrefix+passengerPhone;
//    }
//
//    private String generatorTokenKey(String phone, String identity,String tokenType){
//        return tokenPrefix + phone + "-" + identity+"-"+tokenType;
//    }

    /**
     * 校验验证码
     * @param passengerPhone 手机号
     * @param verificationCode 验证码
     * @return
     */
    public ResponseResult checkCode(String passengerPhone, String verificationCode){

        //去redis读取验证码
        //生成key
        String key = RedisPrefixUtils.generatorKeyByPhone(passengerPhone);
        //根据key获取value
        String codeValue = stringRedisTemplate.opsForValue().get(key);
        //String codeValue = "506143";

        //校验验证码

        if(StringUtils.isBlank(codeValue)){
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(),CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }
        if(!verificationCode.trim().equals(codeValue.trim())){
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(),CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }
        //判断原来是否有用户，有用户登录，没用户插入并登录

        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setPassengerPhone(passengerPhone);
        servicePassengerUserClient.loginOrRegister(verificationCodeDTO);

        //颁发令牌
        String accessToken = JwtUtils.generatorToken(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.ACCESS_TOKEN);
        String refreshToken = JwtUtils.generatorToken(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.REFRESH_TOKEN);

        String accessTokenKey = RedisPrefixUtils.generatorTokenKey(passengerPhone,IdentityConstant.PASSENGER_IDENTITY,TokenConstant.ACCESS_TOKEN);
        stringRedisTemplate.opsForValue().set(accessTokenKey,accessToken,30,TimeUnit.DAYS);

        String refreshTokenKey = RedisPrefixUtils.generatorTokenKey(passengerPhone,IdentityConstant.PASSENGER_IDENTITY,TokenConstant.REFRESH_TOKEN);
        stringRedisTemplate.opsForValue().set(refreshTokenKey,refreshToken,31,TimeUnit.DAYS);


        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);
    }

}
