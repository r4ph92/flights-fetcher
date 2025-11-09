package ca.qc.cegep.ff.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ca.qc.cegep.ff.AbstractIT;

import org.junit.jupiter.api.Test;

public class FlightInfoServiceIT extends AbstractIT {
    @Test
    void testGetFlightInfo() throws Exception {
        setupAviationMock();
        flightInfoService.fetchAndPublishFlight(0.1234, 43.5432);
        verify(mqttOutboundChannel, times(1)).send(any(), anyLong());
    }
}
