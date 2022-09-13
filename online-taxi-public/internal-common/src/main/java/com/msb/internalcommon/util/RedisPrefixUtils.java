package com.msb.internalcommon.util;

public class RedisPrefixUtils {

    //验证码前缀
    public static String verificationCodePrefix = "passenger-verification-code-";

    public static String tokenPrefix = "token-";

    public static String generatorKeyByPhone(String passengerPhone){
        return verificationCodePrefix+passengerPhone;
    }

    public static String generatorTokenKey(String phone, String identity,String tokenType){
        return tokenPrefix + phone + "-" + identity+"-"+tokenType;
    }
}
