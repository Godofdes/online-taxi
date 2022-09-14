package com.msb.service;

import com.msb.internalcommon.constant.CommonStatusEnum;
import com.msb.internalcommon.dto.PriceRule;
import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.request.ForecastPriceDTO;
import com.msb.internalcommon.response.DirectionResponse;
import com.msb.internalcommon.response.ForecastPriceResponse;
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
        BigDecimal price = new BigDecimal(0);

        //起步价
        Double startFare = priceRule.getStartFare();
        BigDecimal startFareDecimal = new BigDecimal(startFare);
        price = price.add(startFareDecimal);

        //里程费
        //总里程
        BigDecimal distanceDecimal = new BigDecimal(distance);
        BigDecimal distanceMileDecimal = distanceDecimal.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
        //起步里程
        Integer startMile = priceRule.getStartMile();
        BigDecimal startMileDecimal = new BigDecimal(startMile);
        //相减
        double distanceSubtract = distanceMileDecimal.subtract(startMileDecimal).doubleValue();
        //是否小于0
        Double mile = distanceSubtract<0?0:distanceSubtract;

        //里程数*里程单价=里程费用
        BigDecimal mileDecimal = new BigDecimal(mile);
        Double unitPricePerMile = priceRule.getUnitPricePerMile();
        BigDecimal unitPricePerMileDecimal = new BigDecimal(unitPricePerMile);
        BigDecimal mileFare = mileDecimal.multiply(unitPricePerMileDecimal).setScale(2,BigDecimal.ROUND_HALF_UP);
        price = price.add(mileFare);

        //时长费
        //总时长，分钟
        BigDecimal time = new BigDecimal(duration);
        BigDecimal timeMinuteDecimal = time.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
        //时长单价
        Double unitPricePerMinute = priceRule.getUnitPricePerMinute();
        BigDecimal unitPricePerMinuteDecimal = new BigDecimal(unitPricePerMinute);
        //总时长*时长单价=时长费
        BigDecimal timeFare = timeMinuteDecimal.multiply(unitPricePerMileDecimal);
        price = price.add(timeFare);

        return price.doubleValue();

    }

}
