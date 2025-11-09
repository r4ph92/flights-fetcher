package ca.qc.cegep.ff.mqtt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ca.qc.cegep.ff.AbstractIT;

import org.junit.jupiter.api.Test;

public class MqttServiceIT extends AbstractIT {

    @Test
    void testPubSub() {
        mqttPublisher.publish("Hello world!");
        verify(mqttOutboundChannel, times(1)).send(any(), anyLong());
    }
}
