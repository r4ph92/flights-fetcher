package ca.qc.cegep.ff.mqtt;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MqttService {
    private static final String CLIENT_ID = "FlightsFetcher";
    private final MqttClientConfig clientConfig;

    private MqttClient client;

    @PostConstruct
    public void init() throws Exception {
        String broker = clientConfig.getBrokerProtocol() + "://" + clientConfig.getBrokerHost() + ":" + clientConfig.getBrokerPort();
        client = new MqttClient(broker, CLIENT_ID);
        client.setCallback(new MqttCallback() {
            public void connectComplete(boolean reconnect, String serverURI) {
            }

            public void disconnected(MqttDisconnectResponse disconnectResponse) {
            }

            public void deliveryComplete(IMqttToken token) {
            }

            public void messageArrived(String topic, MqttMessage message) {
            }

            public void mqttErrorOccurred(MqttException exception) {
            }

            public void authPacketArrived(int reasonCode, MqttProperties properties) {
            }
        });
    }

    public void sendMessage(String payload) {
        try {
            client.connect(new MqttConnectionOptions());
            MqttMessage message = new MqttMessage(payload.getBytes(UTF_8));
            message.setQos(1);
            client.publish(clientConfig.getFlightsTopic(), message);

            client.disconnect();
        } catch (MqttException e) {
            System.err.println("Unable to publish MQTT message: " + e.getMessage());
        }
    }
}
