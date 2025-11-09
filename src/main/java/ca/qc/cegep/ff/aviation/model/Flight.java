package ca.qc.cegep.ff.aviation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Flight {
    private String iataNumber;
    private String icaoNumber;
    private String number;
}
