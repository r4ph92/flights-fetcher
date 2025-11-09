package ca.qc.cegep.ff;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

public class MqttPublisherTester {
    public static void main(String[] args) {

        final String host = "441e7aab97774eb1880ed0a70263e990.s1.eu.hivemq.cloud";
        final String username = "flightPub";
        final String password = "Qwerty911";
        final String topic = "topic/iot/flights";

        // create an MQTT client
        final Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(8883)
                .identifier(username)
                .sslWithDefaultConfig()
                .buildBlocking();

        // connect to HiveMQ Cloud with TLS and username/pw
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
                .applySimpleAuth()
                .send();

        System.out.println("Connected successfully");
        client.publishWith()
                .topic(topic)
                .messageExpiryInterval(60)
                .payload(UTF_8.encode("Hello"))
                .send();
        System.out.println("Sent successfully to topic: " + topic);
        client.disconnect();
    }
}
