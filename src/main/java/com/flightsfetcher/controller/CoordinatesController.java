package com.flightsfetcher.controller;

import com.flightsfetcher.model.Coordinates;
import com.flightsfetcher.service.CoordinatesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/coordinates")
public class CoordinatesController {

    private static final Logger logger = LoggerFactory.getLogger(CoordinatesController.class);

    @Autowired
    private CoordinatesService coordinatesService;

    /**
     * Get current coordinates
     */
    @GetMapping
    public ResponseEntity<Coordinates> getCurrentCoordinates() {
        logger.info("GET /api/coordinates - Retrieving current coordinates");
        Coordinates coordinates = coordinatesService.getCurrentCoordinates();
        return ResponseEntity.ok(coordinates);
    }

    /**
     * Update coordinates
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> updateCoordinates(@Valid @RequestBody Coordinates coordinates) {
        logger.info("POST /api/coordinates - Updating coordinates: {}", coordinates);
        
        coordinatesService.updateCoordinates(coordinates);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Coordinates updated successfully");
        response.put("coordinates", coordinates);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update only specific fields of coordinates
     */
    @PatchMapping
    public ResponseEntity<Map<String, Object>> patchCoordinates(@RequestBody Map<String, Double> updates) {
        logger.info("PATCH /api/coordinates - Partial update: {}", updates);
        
        Coordinates current = coordinatesService.getCurrentCoordinates();
        
        if (updates.containsKey("latitude")) {
            current.setLatitude(updates.get("latitude"));
        }
        if (updates.containsKey("longitude")) {
            current.setLongitude(updates.get("longitude"));
        }
        if (updates.containsKey("radius")) {
            current.setRadius(updates.get("radius"));
        }
        
        coordinatesService.updateCoordinates(current);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Coordinates updated successfully");
        response.put("coordinates", current);
        
        return ResponseEntity.ok(response);
    }
}
