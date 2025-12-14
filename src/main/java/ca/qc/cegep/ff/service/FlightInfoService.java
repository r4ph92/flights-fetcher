package ca.qc.cegep.ff.service;

import ca.qc.cegep.ff.aviation.AviationEdgeService;
import ca.qc.cegep.ff.aviation.model.FlightResponse;
import ca.qc.cegep.ff.mqtt.MqttService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlightInfoService {
    private static final List<Integer> DISTANCES = List.of(25, 50, 75, 100);
    private static final String NO_FLIGHT = "No flight!";

    private final MqttService mqttService;
    private final AviationEdgeService aviationEdgeService;

    public void onDemandFetchAndPublishFlight(String lng, String lat) {
        Optional<FlightResponse> response = getValidResponse(lng, lat);
        String message;
        if (response.isEmpty()) {
            message = NO_FLIGHT;
        } else {
            message = response.get().format();
        }
        System.out.println("Publishing flight: " + message);
        mqttService.sendMessage(message);
    }

    public void fetchAndPublishFlight() {
        Optional<FlightResponse> response = getValidResponse();
        String message;
        if (response.isEmpty()) {
            message = NO_FLIGHT;
        } else {
            message = response.get().format();
        }
        System.out.println("Publishing flight: " + message);
        mqttService.sendMessage(message);
    }

    private Optional<FlightResponse> getValidResponse() {
        FlightResponse response = null;
        for (Integer distance : DISTANCES) {
            List<FlightResponse> flights = aviationEdgeService.getFlights(distance);
            for (FlightResponse flight : flights) {
                if (flight.isValid()) {
                    response = flight;
                    break;
                }
            }
            if (response != null) {
                break;
            }
        }
        return Optional.ofNullable(response);
    }

    private Optional<FlightResponse> getValidResponse(String lng, String lat) {
        FlightResponse response = null;
        for (Integer distance : DISTANCES) {
            List<FlightResponse> flights = aviationEdgeService.getFlights(distance, lng, lat);
            for (FlightResponse flight : flights) {
                if (flight.isValid()) {
                    response = flight;
                    break;
                }
            }
            if (response != null) {
                break;
            }
        }
        return Optional.ofNullable(response);
    }
}
