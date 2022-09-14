package com.msb.apipassenger.controller;

import com.msb.apipassenger.service.ForecastPriceService;
import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.request.ForecastPriceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ForecastPriceController {

    @Autowired
    private ForecastPriceService forecastPriceService;

    @GetMapping("/forecast-price")
    public ResponseResult forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO){
        log.info("controller "+"出发地经度"+forecastPriceDTO.getDepLongitude()+"出发地维度"+forecastPriceDTO.getDepLatitude());
        log.info("controller "+"目的地经度"+forecastPriceDTO.getDestLongitude()+"目的地维度"+forecastPriceDTO.getDestLatitude());

        String depLongitude = forecastPriceDTO.getDepLongitude();
        String depLatitude = forecastPriceDTO.getDepLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();
        String destLatitude = forecastPriceDTO.getDestLatitude();

        return forecastPriceService.forecastPrice(depLongitude,depLatitude,destLongitude,destLatitude);
    }
}
