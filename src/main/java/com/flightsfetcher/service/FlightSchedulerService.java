package com.flightsfetcher.service;

import com.flightsfetcher.client.FlightApiClient;
import com.flightsfetcher.model.Coordinates;
import com.flightsfetcher.model.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(FlightSchedulerService.class);

    @Autowired
    private FlightApiClient flightApiClient;

    @Autowired
    private MqttPublisherService mqttPublisherService;

    @Autowired
    private CoordinatesService coordinatesService;

    /**
     * Scheduled job to fetch flights and publish to MQTT
     * Runs at fixed rate defined in application.yml
     */
    @Scheduled(fixedRateString = "${flight.scheduler.fixed-rate}")
    public void fetchAndPublishFlights() {
        logger.info("Starting scheduled flight fetch job");
        
        try {
            // Get current coordinates
            Coordinates coordinates = coordinatesService.getCurrentCoordinates();
            
            logger.info("Fetching flights for coordinates: lat={}, lon={}, radius={}km",
                    coordinates.getLatitude(), coordinates.getLongitude(), coordinates.getRadius());
            
            // Fetch flights from API
            List<Flight> flights = flightApiClient.fetchFlights(coordinates);
            
            // Publish to MQTT
            if (!flights.isEmpty()) {
                mqttPublisherService.publishFlights(flights);
                logger.info("Completed flight fetch job - published {} flights", flights.size());
            } else {
                logger.info("No flights found in the specified area");
            }
            
        } catch (Exception e) {
            logger.error("Error in scheduled flight fetch job: {}", e.getMessage(), e);
        }
    }
}
