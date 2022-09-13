package com.msb.apipassenger.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.msb.internalcommon.dto.PassengerUser;
import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.dto.TokenResult;
import com.msb.internalcommon.util.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public ResponseResult getUserByAccessToken(String accessToken){

        //解析token得到手机号
        TokenResult tokenResult = JwtUtils.parseToken(accessToken);
        String phone = tokenResult.getPhone();

        //根据手机号查询信息
        PassengerUser passengerUser = new PassengerUser();
        passengerUser.setPassengerName("");
        passengerUser.setProfilePhoto("");

        return ResponseResult.success(passengerUser);

    }

}
