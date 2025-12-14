package ca.qc.cegep.ff.http;

import ca.qc.cegep.ff.job.FlightFetcher;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CoordinatesController {
    private final FlightFetcher flightFetcher;

    @PostMapping("/coordinates")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void postCoordinates(@RequestParam("lng") double lng, @RequestParam("lat") double lat) {
        System.out.println("Received request for lng=" + lng + " and lat=" + lat + ".");
        flightFetcher.onDemand(String.valueOf(lng), String.valueOf(lat));
    }

}
