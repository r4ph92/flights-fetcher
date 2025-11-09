package ca.qc.cegep.ff.mqtt;

import ca.qc.cegep.ff.AbstractIT;

import org.junit.jupiter.api.Test;

public class MqttServiceIT extends AbstractIT {

    @Test
    void testPubSub() {
        mqttService.sendMessage("Hello world!");
    }
}
