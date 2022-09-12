package com.msb.internalcommon.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    //SIGN
    private static final String SIGN = "hdahdfnqoafq";

    private static final String JWT_KEY = "passengerPhone";

    //生成token
    public String generatorToken(String passengerPhone){
        Map<String, String> map = new HashMap<>();
        map.put(JWT_KEY,passengerPhone);

        //token过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();

        //整合map
        map.forEach(
                (k,v) -> {
                    builder.withClaim(k,v);
                }
        );
        //整合过期时间
        builder.withExpiresAt(date);
        //生成token
        String sign = builder.sign(Algorithm.HMAC256(SIGN));

        return sign;
    }


    //解析token
    public static String parseToken(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(token)).build().verify(token);
        return verify.getClaim("passengerPhone").toString();
    }


}
