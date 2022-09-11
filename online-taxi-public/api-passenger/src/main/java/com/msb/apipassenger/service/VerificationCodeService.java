package com.msb.apipassenger.service;

import com.msb.apipassenger.remote.ServiceVerificationCodeClient;
import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.response.NumberCodeResponse;
import net.sf.json.JSONObject;
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

    //验证码前缀
    private String verificationCodePrefix = "passenger-verification-code-";

    public String generatorCode(String passengerPhone){
        //调用验证码服务，获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(6);

        int numberCode = numberCodeResponse.getData().getNumberCode();

        //存入redis
        String key = verificationCodePrefix+passengerPhone;
        stringRedisTemplate.opsForValue().set(key,numberCode+"",2, TimeUnit.MINUTES);

        //返回值
        JSONObject result = new JSONObject();
        result.put("code",1);
        result.put("message","success");
//        ResponseResult<Object> result = new ResponseResult<>();
//        result.setCode(1).setMessage("success");
        return result.toString();

    }

}
