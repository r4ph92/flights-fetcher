package ca.qc.cegep.ff.aviation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "aviation-edge")
@Getter
@Setter
public class AviationEdgeClientConfig {
    private String host;
    private String protocol;
    private String apiKey;
    private String apiContext;
}
