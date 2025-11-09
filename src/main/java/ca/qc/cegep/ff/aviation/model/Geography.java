package ca.qc.cegep.ff.aviation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Geography {
    private Double altitude;
    private Double direction;
    private Double latitude;
    private Double longitude;
}
