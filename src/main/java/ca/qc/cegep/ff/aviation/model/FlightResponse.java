package ca.qc.cegep.ff.aviation.model;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class FlightResponse {
    private Aircraft aircraft;
    private Codes airline;
    private Codes arrival;
    private Codes departure;
    private Flight flight;
    private Geography geography;
    private Speed speed;
    private String status;
    private System system;

    public String format() {
        return Optional.ofNullable(departure).map(Codes::getIataCode).orElse("?")
                + "->"
                + Optional.ofNullable(arrival).map(Codes::getIataCode).orElse("?")
                + " "
                + Optional.ofNullable(flight).map(Flight::getIataNumber).orElse("?")
                + " "
                + Optional.ofNullable(aircraft).map(Codes::getIataCode).orElse("?");

    }

    public boolean isValid() {
        return flight != null && flight.getIataNumber() != null
                && aircraft != null && aircraft.getIataCode() != null
                && departure != null && departure.getIataCode() != null
                && arrival != null && arrival.getIataCode() != null;
    }
}
