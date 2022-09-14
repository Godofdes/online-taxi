package com.msb.internalcommon.util;

import java.math.BigDecimal;

public class BigDecimalUtils {

    //加法
    public static double add(double v1, double v2){
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);

        return b1.add(b2).doubleValue();
    }
    //减法
    public static double subtractBig(double v1, double v2){
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);

        return v1<v2?0:b1.subtract(b2).doubleValue();
    }
    //乘法
    public static double multiply(double v1, double v2){
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);

        return b1.multiply(b2).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    //除法
    public static double divide(double v1, double v2){
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);

        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
