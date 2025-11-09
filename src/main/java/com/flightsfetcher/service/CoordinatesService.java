package com.flightsfetcher.service;

import com.flightsfetcher.model.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CoordinatesService {

    private static final Logger logger = LoggerFactory.getLogger(CoordinatesService.class);

    private Coordinates currentCoordinates;

    public CoordinatesService(
            @Value("${flight.coordinates.latitude}") Double latitude,
            @Value("${flight.coordinates.longitude}") Double longitude,
            @Value("${flight.coordinates.radius}") Double radius) {
        this.currentCoordinates = new Coordinates(latitude, longitude, radius);
        logger.info("Initialized with default coordinates: lat={}, lon={}, radius={}km",
                latitude, longitude, radius);
    }

    /**
     * Get the current coordinates
     */
    public Coordinates getCurrentCoordinates() {
        return currentCoordinates;
    }

    /**
     * Update the current coordinates
     */
    public void updateCoordinates(Coordinates coordinates) {
        logger.info("Updating coordinates from lat={}, lon={}, radius={}km to lat={}, lon={}, radius={}km",
                this.currentCoordinates.getLatitude(),
                this.currentCoordinates.getLongitude(),
                this.currentCoordinates.getRadius(),
                coordinates.getLatitude(),
                coordinates.getLongitude(),
                coordinates.getRadius());
        this.currentCoordinates = coordinates;
    }
}
