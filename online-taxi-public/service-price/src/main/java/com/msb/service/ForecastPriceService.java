package com.msb.service;

import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.request.ForecastPriceDTO;
import com.msb.internalcommon.response.DirectionResponse;
import com.msb.internalcommon.response.ForecastPriceResponse;
import com.msb.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private ServiceMapClient serviceMapClient;

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


        ResponseResult<DirectionResponse> driving = serviceMapClient.driving(forecastPriceDTO);

        ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
        Integer distance = driving.getData().getDistance();
        Integer duration = driving.getData().getDuration();
        log.info("distance: "+distance+" duration: "+duration);

        forecastPriceResponse.setPrice(1234);

        return ResponseResult.success(forecastPriceResponse);
    }

}
