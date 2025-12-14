package ca.qc.cegep.ff.aviation;

import ca.qc.cegep.ff.aviation.model.FlightResponse;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AviationEdgeService {
    private static final String FLIGHTS_PATH = "/flights";
    private static final String APIKEY_PARAM = "key";
    private static final String LNG_PARAM = "lng";
    private static final String LNG_VALUE = "-73.7408";
    private static final String LAT_PARAM = "lat";
    private static final String LAT_VALUE = "45.4706";
    private static final String LIMIT_PARAM = "limit";
    private static final int LIMIT_VALUE = 10;
    private static final String DISTANCE_PARAM = "distance";
    private static final String STATUS_PARAM = "status";
    private static final String STATUS_VALUE = "en-route";

    private final AviationEdgeClientConfig config;
    private RestClient restClient;

    @PostConstruct
    public void init() {
        restClient = RestClient.builder()
                .baseUrl(config.getProtocol() + "://" + config.getHost() + "/" + config.getApiContext())
                .build();
        System.out.println("Baseurl: " + config.getProtocol() + "://" + config.getHost() + "/" + config.getApiContext());
    }

    public List<FlightResponse> getFlights(int distance) {
        return getFlights(distance, LNG_VALUE, LAT_VALUE);
    }

    public List<FlightResponse> getFlights(int distance, String lng, String lat) {
        System.out.println("Getting flights for distance= " + distance);
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(FLIGHTS_PATH)
                            .queryParam(APIKEY_PARAM, config.getApiKey())
                            .queryParam(LAT_PARAM, lat)
                            .queryParam(LNG_PARAM, lng)
                            .queryParam(LIMIT_PARAM, LIMIT_VALUE)
                            .queryParam(STATUS_PARAM, STATUS_VALUE)
                            .queryParam(DISTANCE_PARAM, distance)
                            .build())
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<FlightResponse>>() {
                    });
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return List.of();
    }
}
