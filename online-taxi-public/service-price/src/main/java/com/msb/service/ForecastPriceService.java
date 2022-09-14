package com.msb.service;

import com.msb.internalcommon.constant.CommonStatusEnum;
import com.msb.internalcommon.dto.PriceRule;
import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.request.ForecastPriceDTO;
import com.msb.internalcommon.response.DirectionResponse;
import com.msb.internalcommon.response.ForecastPriceResponse;
import com.msb.internalcommon.util.BigDecimalUtils;
import com.msb.mapper.PriceRuleMapper;
import com.msb.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    private PriceRuleMapper priceRuleMapper;

    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude){

        log.info("service "+"出发地经度: "+depLongitude+"出发地维度: "+depLatitude);
        log.info("service "+"目的地经度: "+destLongitude+"目的地维度: "+destLatitude);

        log.info("service "+"调用地图服务，查询距离和时长");

        log.info("service "+"读取计价规则");

        log.info("service "+"根据距离和时长计算价格");


        //depLongitude, depLatitude, destLongitude, destLatitude
        ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
        forecastPriceDTO.setDepLongitude(depLongitude);
        forecastPriceDTO.setDepLatitude(depLatitude);
        forecastPriceDTO.setDestLongitude(destLongitude);
        forecastPriceDTO.setDestLatitude(destLatitude);

        //使用feign调用service-map接口
        ResponseResult<DirectionResponse> driving = serviceMapClient.driving(forecastPriceDTO);

        ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
        Integer distance = driving.getData().getDistance();
        Integer duration = driving.getData().getDuration();
        log.info("distance: "+distance+" duration: "+duration);

        //读取计价规则
        Map<String,Object> map = new HashMap<>();
        map.put("city_code","110000");
        map.put("vehicle_type","1");
        List<PriceRule> priceRules = priceRuleMapper.selectByMap(map);
        if(priceRules.size()==0){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(),CommonStatusEnum.PRICE_RULE_EMPTY.getValue());
        }
        PriceRule priceRule = priceRules.get(0);

        Double price = getPrice(distance, duration, priceRule);
        forecastPriceResponse.setPrice(price);

        return ResponseResult.success(forecastPriceResponse);
    }

    private Double getPrice(Integer distance, Integer duration, PriceRule priceRule){
        double price = 0.0;

        //起步价
        Double startFare = priceRule.getStartFare();
        price = BigDecimalUtils.add(price,startFare);

        //里程费
        //总里程
        double distanceMile = BigDecimalUtils.divide((double)distance, 1000.0);

        //起步里程
        Integer startMile = priceRule.getStartMile();

        //相减
        double distanceSubtract = BigDecimalUtils.subtractBig(distanceMile,(double)startMile);


        //里程数*里程单价=里程费用

        double unitPricePerMile = priceRule.getUnitPricePerMile();
        double mileFare = BigDecimalUtils.multiply(distanceSubtract,unitPricePerMile);

        price = BigDecimalUtils.add(price,mileFare);

        //时长费
        //总时长，分钟
        double timeMinute = BigDecimalUtils.divide((double)duration,60.0);
        //时长单价
        double unitPricePerMinute = priceRule.getUnitPricePerMinute();
        //总时长*时长单价=时长费
        double timeFare = BigDecimalUtils.multiply(timeMinute,unitPricePerMile);
        price = BigDecimalUtils.add(price,timeFare);

        return price;

    }

}
