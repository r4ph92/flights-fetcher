package com.flightsfetcher.service;

import com.flightsfetcher.model.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesServiceTest {

    private CoordinatesService coordinatesService;

    @BeforeEach
    void setUp() {
        coordinatesService = new CoordinatesService(40.7128, -74.0060, 50.0);
    }

    @Test
    void testGetCurrentCoordinates() {
        Coordinates coordinates = coordinatesService.getCurrentCoordinates();
        
        assertNotNull(coordinates);
        assertEquals(40.7128, coordinates.getLatitude());
        assertEquals(-74.0060, coordinates.getLongitude());
        assertEquals(50.0, coordinates.getRadius());
    }

    @Test
    void testUpdateCoordinates() {
        Coordinates newCoordinates = new Coordinates(51.5074, -0.1278, 75.0);
        coordinatesService.updateCoordinates(newCoordinates);
        
        Coordinates current = coordinatesService.getCurrentCoordinates();
        assertEquals(51.5074, current.getLatitude());
        assertEquals(-0.1278, current.getLongitude());
        assertEquals(75.0, current.getRadius());
    }
}
