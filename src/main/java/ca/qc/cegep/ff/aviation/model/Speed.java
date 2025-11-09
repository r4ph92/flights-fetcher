package ca.qc.cegep.ff.aviation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Speed {
    private Double horizontal;
    private Double isGround;
    private Double vspeed;
}
