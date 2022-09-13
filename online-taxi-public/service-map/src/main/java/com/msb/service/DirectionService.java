package com.msb.service;

import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.response.DirectionResponse;
import com.msb.remote.MapDirectionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirectionService {

    @Autowired
    private MapDirectionClient mapDirectionClient;

    /**
     * 根据出发地和目的地经纬度，获取距离和时长
     * @param depLongitude
     * @param depLatitude
     * @param destLongitude
     * @param destLatitude
     * @return
     */
    public ResponseResult driving(String depLongitude, String depLatitude, String destLongitude, String destLatitude){

//        DirectionResponse directionResponse = new DirectionResponse();
//        directionResponse.setDuration(1);
//        directionResponse.setDistance(1234);

        //调用第三方地图接口
        DirectionResponse directionResponse = mapDirectionClient.direction(depLongitude, depLatitude, destLongitude, destLatitude);


        return ResponseResult.success(directionResponse);
    }

}
