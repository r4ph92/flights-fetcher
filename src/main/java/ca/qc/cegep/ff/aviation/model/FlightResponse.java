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
        return Optional.ofNullable(airline).map(Codes::getIcaoCode).orElse("?")
                + " "
                + Optional.ofNullable(flight).map(Flight::getIataNumber).orElse("?")
                + "("
                + Optional.ofNullable(aircraft).map(Aircraft::getIcaoCode).orElse("?")
                + ") "
                + Optional.ofNullable(departure).map(Codes::getIcaoCode).orElse("?")
                + "->"
                + Optional.ofNullable(arrival).map(Codes::getIcaoCode).orElse("?");
    }

    public boolean isValid() {
        return airline != null && airline.getIcaoCode() != null
                && flight != null && flight.getIataNumber() != null
                && aircraft != null && aircraft.getIcaoCode() != null
                && departure != null && departure.getIcaoCode() != null
                && arrival != null && arrival.getIcaoCode() != null
                && !status.equalsIgnoreCase("landed")
                && !status.equalsIgnoreCase("unknown");
    }
}
