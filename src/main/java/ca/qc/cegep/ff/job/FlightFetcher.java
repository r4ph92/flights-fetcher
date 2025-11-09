package ca.qc.cegep.ff.job;

import ca.qc.cegep.ff.config.Config;
import ca.qc.cegep.ff.service.FlightInfoService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FlightFetcher {
    private final Config cfg;
    private final FlightInfoService flightInfoService;

    private double lat;
    private double lng;

    @PostConstruct
    public void init() {
        lat = cfg.getLat();
        lng = cfg.getLng();
    }

    public void updateCoordinates(double lng, double lat) {
        this.lat = lat;
        this.lng = lng;
        refresh();
    }

    @Scheduled(fixedRateString = "${default.job-rate}", initialDelayString = "${default.job-rate}")
    public void refresh() {
        System.out.println("Refreshing Flight Info with lat: " + lat + " and lng: " + lng);
        flightInfoService.fetchAndPublishFlight(lng, lat);
    }
}
