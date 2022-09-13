package com.msb.controller;

import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.request.ForecastPriceDTO;
import com.msb.service.ForecastPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ForecastController {

    @Autowired
    private ForecastPriceService forecastPriceService;

    @GetMapping("/forecast-price")
    public ResponseResult forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO){
        String depLongitude = forecastPriceDTO.getDepLongitude();
        String depLatitude = forecastPriceDTO.getDepLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();
        String destLatitude = forecastPriceDTO.getDestLatitude();

        return forecastPriceService.forecastPrice(depLongitude,depLatitude,destLongitude,destLatitude);
    }

}
