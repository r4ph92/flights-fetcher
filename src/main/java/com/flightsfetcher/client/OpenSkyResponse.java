package com.flightsfetcher.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenSkyResponse {
    
    @JsonProperty("time")
    private Long time;
    
    @JsonProperty("states")
    private List<List<Object>> states;
}
