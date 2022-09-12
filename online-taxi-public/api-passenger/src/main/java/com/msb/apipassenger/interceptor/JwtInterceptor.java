package com.msb.apipassenger.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.dto.TokenResult;
import com.msb.internalcommon.util.JwtUtils;
import net.sf.json.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.SignatureException;


public class JwtInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean result = true;
        String resultString = "";


        String token = request.getHeader("Authorization");
        System.out.println(token);
        TokenResult tokenResult = new TokenResult();

        try {
            tokenResult = JwtUtils.parseToken(token);
        }catch (SignatureVerificationException e){
            resultString = "token sign error";
            result = false;
        }catch (TokenExpiredException e){
            resultString = "token time out";
            result = false;
        }catch (AlgorithmMismatchException e){
            resultString = "token AlgorithmMismatchException";
            result = false;
        }

        if(tokenResult==null){
            resultString = "token invalid";
            result = false;
        }else {
            String phone = tokenResult.getPhone();
            String identity = tokenResult.getIdentity();
            System.out.println("phone:"+phone+"-"+"identity:"+identity);
        }

        if(!result){
            PrintWriter writer = response.getWriter();
            writer.print(JSONObject.fromObject(ResponseResult.fail(resultString)).toString());
        }

        return result;
    }
}
