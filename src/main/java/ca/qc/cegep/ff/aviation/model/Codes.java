package ca.qc.cegep.ff.aviation.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Codes {
    private String iataCode;
    private String icaoCode;
}
