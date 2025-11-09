package ca.qc.cegep.ff.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "mqtt")
@Getter
@Setter
public class MqttClientConfig {
    private String brokerProtocol;
    private String brokerHost;
    private int brokerPort;
    private String username;
    private String password;
    private String flightsTopic;
}
