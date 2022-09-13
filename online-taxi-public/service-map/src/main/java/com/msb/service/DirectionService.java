package com.msb.service;

import com.msb.internalcommon.dto.ResponseResult;
import com.msb.internalcommon.response.DistanceResponse;
import org.springframework.stereotype.Service;

@Service
public class DirectionService {

    /**
     * 根据出发地和目的地经纬度，获取距离和时长
     * @param depLongitude
     * @param depLatitude
     * @param destLongitude
     * @param destLatitude
     * @return
     */
    public ResponseResult driving(String depLongitude, String depLatitude, String destLongitude, String destLatitude){

        DistanceResponse distanceResponse = new DistanceResponse();
        distanceResponse.setDuration(1);
        distanceResponse.setDistance(1234);


        return ResponseResult.success(distanceResponse);
    }

}
