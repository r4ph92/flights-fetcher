package ca.qc.cegep.ff.mqtt;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MqttService {
    private final MqttClientConfig clientConfig;

    private Mqtt5BlockingClient client;

    @PostConstruct
    public void init() {
        client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(clientConfig.getBrokerHost())
                .serverPort(clientConfig.getBrokerPort())
                .sslWithDefaultConfig()
                .buildBlocking();
    }

    public void sendMessage(String message) {
        client.connectWith()
                .simpleAuth()
                .username(clientConfig.getUsername())
                .password(UTF_8.encode(clientConfig.getPassword()))
                .applySimpleAuth()
                .send();
        System.out.println("MQTT client connected successfully");

        client.publishWith()
                .topic(clientConfig.getFlightsTopic())
                .payload(UTF_8.encode(message))
                .send();
        System.out.println("MQTT message sent sent successfully on topic: " + clientConfig.getFlightsTopic());

        client.disconnect();
        System.out.println("MQTT client disconnected successfully");
    }
}
