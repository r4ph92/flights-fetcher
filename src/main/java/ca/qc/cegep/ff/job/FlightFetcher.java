package ca.qc.cegep.ff.job;

import ca.qc.cegep.ff.service.FlightInfoService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FlightFetcher {
    private final FlightInfoService flightInfoService;

    @Scheduled(fixedRateString = "${default.job-rate}")
    public void refresh() {
        System.out.println("Refreshing Flight Info");
        flightInfoService.fetchAndPublishFlight();
    }
}
