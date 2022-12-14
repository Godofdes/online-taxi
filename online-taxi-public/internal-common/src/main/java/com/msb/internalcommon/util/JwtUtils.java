package com.msb.internalcommon.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.msb.internalcommon.constant.IdentityConstant;
import com.msb.internalcommon.constant.TokenConstant;
import com.msb.internalcommon.dto.TokenResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    //SIGN
    private static final String SIGN = "hdahdfnqoafq";

    private static final String JWT_KEY_PHONE = "phone";

    private static final String JWT_KEY_IDENTITY = "identity";

    private static final String JWT_TOKEN_TYPE = "tokenType";

    private static final String JWT_TOKEN_TIME = "tokenTime";



    //生成token
    public static String generatorToken(String passengerPhone,String identity,String tokenType){
        Map<String, String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE,passengerPhone);
        map.put(JWT_KEY_IDENTITY,identity);
        map.put(JWT_TOKEN_TYPE,tokenType);

        //token过期时间
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE,1);
//        Date date = calendar.getTime();
        map.put(JWT_TOKEN_TIME,Calendar.getInstance().getTime().toString());

        JWTCreator.Builder builder = JWT.create();

        //整合map
        map.forEach(
                (k,v) -> {
                    builder.withClaim(k,v);
                }
        );
        //整合过期时间
        //builder.withExpiresAt(date);
        //生成token
        String sign = builder.sign(Algorithm.HMAC256(SIGN));

        return sign;
    }

    //解析token
    public static TokenResult parseToken(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        String phone = verify.getClaim(JWT_KEY_PHONE).asString();
        String identity = verify.getClaim(JWT_KEY_IDENTITY).asString();

        TokenResult tokenResult = new TokenResult();
        tokenResult.setPhone(phone);
        tokenResult.setIdentity(identity);
        return tokenResult;
    }

//    public static void main(String[] args) {
//        String token = generatorToken("18800178665", IdentityConstant.PASSENGER_IDENTITY, TokenConstant.REFRESH_TOKEN);
//        System.out.println(token);
//        TokenResult tokenResult = parseToken(token);
//        System.out.println(tokenResult.getPhone()+tokenResult.getIdentity());
//    }

    public static TokenResult checkToken(String token){
        TokenResult tokenResult = null;
        try {
            tokenResult = parseToken(token);
        }catch (Exception e){

        }
        tokenResult = parseToken(token);
        return tokenResult;
    }

}
