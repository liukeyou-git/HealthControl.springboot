package com.example.web.tools.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LatLng {
    @JsonProperty("longitude")
    public  double longitude;
    @JsonProperty("longitude")
    public double latitude;
}
