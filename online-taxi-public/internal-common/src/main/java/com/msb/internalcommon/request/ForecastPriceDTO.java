package com.msb.internalcommon.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ForecastPriceDTO {
    private String depLongitude;
    private String depLatitude;
    private String destLongitude;
    private String destLatitude;

}
