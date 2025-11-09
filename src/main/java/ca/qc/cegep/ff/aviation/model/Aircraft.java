package ca.qc.cegep.ff.aviation.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class Aircraft extends Codes {
    private String icao24;
    private String regNumber;
}
