package ca.qc.cegep.ff.aviation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class System {
    private Integer squawk;
    private Long updated;
}
