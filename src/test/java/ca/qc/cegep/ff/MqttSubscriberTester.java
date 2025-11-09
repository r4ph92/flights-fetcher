package ca.qc.cegep.ff;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

public class MqttSubscriberTester {
    public static void main(String[] args) {

        final String host = "441e7aab97774eb1880ed0a70263e990.s1.eu.hivemq.cloud";
        final String username = "flightSub";
        final String password = "Qwerty911";
        final String topic = "topic/iot/flights";

        // create an MQTT client
        final Mqtt5AsyncClient client = MqttClient.builder()
                .useMqttVersion5()
                .identifier(username)
                .serverHost(host)
                .serverPort(8883)
                .sslWithDefaultConfig()
                .buildAsync();

        // connect to HiveMQ Cloud with TLS and username/pw
        client.connectWith()
                .simpleAuth()
                    .username(username)
                    .password(UTF_8.encode(password))
                    .applySimpleAuth()
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Connected unsuccessfully:" + throwable.getMessage());
                    } else {
                        System.out.println("Connected successfully");
                    }
                });


        client.subscribeWith()
                .topicFilter(topic)
                .callback(publish -> {
                    System.out.println("Received message: " +
                            publish.getTopic() + " -> " +
                            UTF_8.decode(publish.getPayload().get()));
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        // Handle failure to subscribe
                        System.out.println("Subscribed unsuccessfully:" + throwable.getMessage());
                    } else {
                        System.out.println("Subscribed successfully to: " + topic);
                    }
                });
    }
}
