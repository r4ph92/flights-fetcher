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
    private static final List<Integer> DISTANCES = List.of(5, 10, 25, 50);
    private static final String NO_FLIGHT = "No flight!";

    private final MqttService mqttService;
    private final AviationEdgeService aviationEdgeService;

    public void fetchAndPublishFlight(double lng, double lat) {
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

    private Optional<FlightResponse> getValidResponse(double lng, double lat) {
        FlightResponse response = null;
        for (int i = 0; i < DISTANCES.size(); i++) {
            List<FlightResponse> flights = aviationEdgeService.getFlights(lng, lat, i);
            for (FlightResponse flight : flights) {
                if (flight.isValid()) {
                    response = flight;
                    break;
                }
            }
        }
        return Optional.ofNullable(response);
    }
}
