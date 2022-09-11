package com.msb.controller;

import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.response.NumberCodeResponse;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class NumberCodeController {

    @RequestMapping("/numberCode/{size}")
    public ResponseResult numberCode(@PathVariable("size") int size){

        //生成验证码，即六位随机数
        double mathRandom = (Math.random()*9+1)*(Math.pow(10,size-1));
        int mathInt = (int)mathRandom;

//        JSONObject result = new JSONObject();
//        result.put("code",1);
//        result.put("message","success");
//        JSONObject data = new JSONObject();
//        data.put("numberCode",mathInt);
//        result.put("data",data);

        NumberCodeResponse numberCodeResponse = new NumberCodeResponse();
        numberCodeResponse.setNumberCode(mathInt);

        return ResponseResult.success(numberCodeResponse);
    }
}
