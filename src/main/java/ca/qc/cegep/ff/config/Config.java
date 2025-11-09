package ca.qc.cegep.ff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "default")
@Getter
@Setter
public class Config {
    private double lat;
    private double lng;
}
