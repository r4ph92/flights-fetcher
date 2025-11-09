package com.flightsfetcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    
    private String icao24;          // Unique ICAO 24-bit address
    private String callsign;        // Callsign of the vehicle
    private String originCountry;   // Country name
    private Double longitude;       // WGS-84 longitude in decimal degrees
    private Double latitude;        // WGS-84 latitude in decimal degrees
    private Double baroAltitude;    // Barometric altitude in meters
    private Double velocity;        // Velocity over ground in m/s
    private Double heading;         // True track in decimal degrees
    private Double verticalRate;    // Vertical rate in m/s
    private Long timePosition;      // Unix timestamp (seconds)
    private Long lastContact;       // Unix timestamp (seconds)
    private Boolean onGround;       // Whether aircraft is on ground
}
