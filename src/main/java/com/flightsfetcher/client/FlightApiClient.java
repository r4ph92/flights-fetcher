package com.flightsfetcher.client;

import com.flightsfetcher.model.Coordinates;
import com.flightsfetcher.model.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlightApiClient {

    private static final Logger logger = LoggerFactory.getLogger(FlightApiClient.class);

    private final WebClient webClient;

    public FlightApiClient(@Value("${flight.api.url}") String apiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    /**
     * Fetch flights in the bounding box defined by the coordinates and radius
     */
    public List<Flight> fetchFlights(Coordinates coordinates) {
        try {
            // Calculate bounding box from center point and radius
            double[] bbox = calculateBoundingBox(
                coordinates.getLatitude(),
                coordinates.getLongitude(),
                coordinates.getRadius()
            );

            logger.info("Fetching flights in bounding box: lat_min={}, lon_min={}, lat_max={}, lon_max={}",
                    bbox[0], bbox[1], bbox[2], bbox[3]);

            OpenSkyResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/states/all")
                            .queryParam("lamin", bbox[0])
                            .queryParam("lomin", bbox[1])
                            .queryParam("lamax", bbox[2])
                            .queryParam("lomax", bbox[3])
                            .build())
                    .retrieve()
                    .bodyToMono(OpenSkyResponse.class)
                    .onErrorResume(e -> {
                        logger.error("Error fetching flights from OpenSky API: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .block();

            if (response == null || response.getStates() == null) {
                logger.warn("No flight data received from API");
                return new ArrayList<>();
            }

            return parseFlights(response);

        } catch (Exception e) {
            logger.error("Error fetching flights: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Parse OpenSky API response into Flight objects
     */
    private List<Flight> parseFlights(OpenSkyResponse response) {
        List<Flight> flights = new ArrayList<>();

        for (List<Object> state : response.getStates()) {
            try {
                Flight flight = new Flight();
                
                // OpenSky state vector format:
                // 0: icao24, 1: callsign, 2: origin_country, 3: time_position,
                // 4: last_contact, 5: longitude, 6: latitude, 7: baro_altitude,
                // 8: on_ground, 9: velocity, 10: true_track, 11: vertical_rate
                
                flight.setIcao24(getStringValue(state, 0));
                flight.setCallsign(getStringValue(state, 1));
                flight.setOriginCountry(getStringValue(state, 2));
                flight.setTimePosition(getLongValue(state, 3));
                flight.setLastContact(getLongValue(state, 4));
                flight.setLongitude(getDoubleValue(state, 5));
                flight.setLatitude(getDoubleValue(state, 6));
                flight.setBaroAltitude(getDoubleValue(state, 7));
                flight.setOnGround(getBooleanValue(state, 8));
                flight.setVelocity(getDoubleValue(state, 9));
                flight.setHeading(getDoubleValue(state, 10));
                flight.setVerticalRate(getDoubleValue(state, 11));

                flights.add(flight);
            } catch (Exception e) {
                logger.warn("Error parsing flight data: {}", e.getMessage());
            }
        }

        logger.info("Parsed {} flights from API response", flights.size());
        return flights;
    }

    /**
     * Calculate bounding box from center point and radius in kilometers
     * Returns [lat_min, lon_min, lat_max, lon_max]
     */
    private double[] calculateBoundingBox(double lat, double lon, double radiusKm) {
        // Rough approximation: 1 degree latitude = 111 km
        // 1 degree longitude = 111 km * cos(latitude)
        double latDelta = radiusKm / 111.0;
        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));

        return new double[]{
            lat - latDelta,  // lat_min
            lon - lonDelta,  // lon_min
            lat + latDelta,  // lat_max
            lon + lonDelta   // lon_max
        };
    }

    private String getStringValue(List<Object> state, int index) {
        if (index < state.size() && state.get(index) != null) {
            return state.get(index).toString().trim();
        }
        return null;
    }

    private Double getDoubleValue(List<Object> state, int index) {
        if (index < state.size() && state.get(index) != null) {
            try {
                return Double.parseDouble(state.get(index).toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Long getLongValue(List<Object> state, int index) {
        if (index < state.size() && state.get(index) != null) {
            try {
                return Long.parseLong(state.get(index).toString().split("\\.")[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Boolean getBooleanValue(List<Object> state, int index) {
        if (index < state.size() && state.get(index) != null) {
            return Boolean.parseBoolean(state.get(index).toString());
        }
        return null;
    }
}
