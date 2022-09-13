package com.msb.remote;

import com.msb.internalcommon.constant.AmapConfigConstants;
import com.msb.internalcommon.response.DirectionResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MapDirectionClient {

    @Value("${amap.key}")
    private String amapKey;

    @Autowired
    private RestTemplate restTemplate;

    public DirectionResponse direction(String depLongitude, String depLatitude, String destLongitude, String destLatitude){

        /**
         *?origin=116.481028,39.989643&destination=116.465302,40.004717&extensions=all&output=json&key=eeb09f5cccce0cce0b5435897628725f
         */
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(AmapConfigConstants.DIRECTION_URL);
        urlBuilder.append("?");
        urlBuilder.append("origin="+depLongitude+","+depLatitude);
        urlBuilder.append("&");
        urlBuilder.append("destination="+destLongitude+","+destLatitude);
        urlBuilder.append("&");
        urlBuilder.append("extensions=base");
        urlBuilder.append("&");
        urlBuilder.append("output=json");
        urlBuilder.append("&");
        urlBuilder.append("key="+amapKey);
        ResponseEntity<String> directionEntity = restTemplate.getForEntity(urlBuilder.toString(), String.class);
        String directionString = directionEntity.getBody();
        DirectionResponse directionResponse = parseDirectionEntity(directionString);


        return directionResponse;
    }


    private DirectionResponse parseDirectionEntity(String directionString){
        DirectionResponse directionResponse = null;
        try {
            JSONObject result = JSONObject.fromObject(directionString);
            if (result.has(AmapConfigConstants.DIRECTION_STATUS)){
                int status = result.getInt(AmapConfigConstants.DIRECTION_STATUS);
                if(status == 1){
                    if(result.has(AmapConfigConstants.DIRECTION_ROUTE)){
                        JSONObject routeObject = result.getJSONObject(AmapConfigConstants.DIRECTION_ROUTE);
                        JSONArray pathArray = routeObject.getJSONArray(AmapConfigConstants.DIRECTION_PATHS);
                        JSONObject pathObject = pathArray.getJSONObject(0);
                        int distance = 0;
                        int duration = 0;
                        directionResponse = new DirectionResponse();
                        if(pathObject.has(AmapConfigConstants.DIRECTION_DISTANCE)&&pathObject.has(AmapConfigConstants.DIRECTION_DURATION)){
                            distance = pathObject.getInt(AmapConfigConstants.DIRECTION_DISTANCE);
                            duration = pathObject.getInt(AmapConfigConstants.DIRECTION_DURATION);
                        }
                        directionResponse.setDistance(distance);
                        directionResponse.setDuration(duration);
                    }
                }
            }

        }catch (Exception e){

        }
        return directionResponse;

    }
}
